package baaahs.glshaders

import baaahs.Color
import baaahs.Gadget
import baaahs.ShowContext
import baaahs.gadgets.ColorPicker
import baaahs.gadgets.Slider
import baaahs.getTimeMillis
import baaahs.glsl.GlslRenderer
import baaahs.glsl.Uniform
import com.danielgergely.kgl.*

class CorePlugin : Plugin {
    override val packageName: String = "baaahs.Core"
    override val name: String = "SparkleMotion Core"

    override fun matchUniformProvider(
        name: String,
        uniformPort: Patch.UniformPortRef,
        program: GlslProgram,
        showContext: ShowContext
    ): GlslProgram.UniformProvider? {
        return when (name) {
            "resolution" -> ResolutionProvider()
            "time" -> TimeProvider()
            "uvCoords" -> UvCoordProvider(program)

            "Slider" -> SliderProvider(uniformPort, showContext)
            "ColorPicker" -> ColorPickerProvider(uniformPort, showContext)

            "none" -> null

            else -> throw IllegalArgumentException("unknown type ${name}")
        }
    }


    class ResolutionProvider : GlslProgram.UniformProvider, GlslProgram.ResolutionListener {
        override val supportedTypes: List<String> = listOf("vec2")

        var x = 1f
        var y = 1f

        override fun set(uniform: Uniform) {
            uniform.set(x, y)
        }

        override fun onResolution(x: Float, y: Float) {
            this.x = x
            this.y = y
        }
    }

    class TimeProvider : GlslProgram.UniformProvider {
        override val supportedTypes: List<String> = listOf("float")

        override fun set(uniform: Uniform) {
            val thisTime = (getTimeMillis() and 0x7ffffff).toFloat() / 1000.0f
            uniform.set(thisTime)
        }
    }

    class UvCoordProvider(val program: GlslProgram) : GlslProgram.UniformProvider, GlslRenderer.ArrangementListener {
        override val supportedTypes: List<String> = listOf("sampler2D")

        private val uvCoordTextureId = program.obtainTextureId()
        private val uvCoordTexture = program.gl.check { createTexture() }

        override fun onArrangementChange(arrangement: GlslRenderer.Arrangement) {
            if (arrangement.uvCoords.isEmpty()) return

            val pixWidth = arrangement.pixWidth
            val pixHeight = arrangement.pixHeight
            val floatBuffer = FloatBuffer(arrangement.uvCoords)

            with(program.gl) {
                check { activeTexture(GL_TEXTURE0 + uvCoordTextureId) }
                check { bindTexture(GL_TEXTURE_2D, uvCoordTexture) }
                check { texParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST) }
                check { texParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST) }
                check {
                    texImage2D(
                        GL_TEXTURE_2D, 0,
                        GL_R32F, pixWidth * 2, pixHeight, 0,
                        GL_RED,
                        GL_FLOAT, floatBuffer
                    )
                }
            }
        }

        override fun set(uniform: Uniform) {
            uniform.set(uvCoordTextureId)
        }

        override fun release() {
            program.gl.check { deleteTexture(uvCoordTexture) }
        }
    }

    abstract class GadgetProvider<T : Gadget>(
        uniformPortRef: Patch.UniformPortRef,
        val showContext: ShowContext,
        pGadget: T
    ) : GlslProgram.UniformProvider {
        val gadget: T

        init {
            val varName = uniformPortRef.varName
            val gadgetId = "glsl_$varName"
            gadget = showContext.getGadget(gadgetId, pGadget)
        }
    }

    class SliderProvider(
        uniformPortRef: Patch.UniformPortRef,
        showContext: ShowContext
    ) : GadgetProvider<Slider>(uniformPortRef, showContext,
        Slider(
            uniformPortRef.name.capitalize(),
            initialValue = uniformPortRef.pluginConfig["default"]?.toFloat() ?: 1f,
            minValue = uniformPortRef.pluginConfig["min"]?.toFloat() ?: 0f,
            maxValue = uniformPortRef.pluginConfig["max"]?.toFloat() ?: 1f,
            stepValue = uniformPortRef.pluginConfig["step"]?.toFloat() ?: .01f
            // ,scale = uniformPortRef.pluginConfig["scale"] ?: "linear"
        )
    ) {
        override val supportedTypes: List<String> = listOf("float")

        override fun set(uniform: Uniform) = uniform.set(gadget.value)
    }

    class ColorPickerProvider(
        val uniformPortRef: Patch.UniformPortRef,
        showContext: ShowContext
    ) : GadgetProvider<ColorPicker>(uniformPortRef, showContext,
        ColorPicker(
            uniformPortRef.name.capitalize(),
            initialValue = uniformPortRef.pluginConfig["default"]?.let { Color.from(it) } ?: Color.WHITE
        )
    ) {
        override val supportedTypes: List<String> = listOf("vec3", "vec4")

        override fun set(uniform: Uniform) {
            val color = gadget.color
            when (uniformPortRef.type) {
                "vec3" -> uniform.set(color.redF, color.greenF, color.blueF)
                "vec4" -> uniform.set(color.redF, color.greenF, color.blueF, color.alphaF)
            }
        }
    }
}