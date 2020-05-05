package baaahs.glshaders

import baaahs.Color
import baaahs.Gadget
import baaahs.ShowContext
import baaahs.gadgets.ColorPicker
import baaahs.gadgets.Slider
import baaahs.getTimeMillis
import baaahs.glshaders.GlslProgram.DataSource
import baaahs.glshaders.GlslProgram.DataSourceProvider
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
    ): DataSourceProvider? {
        return when (name) {
            "resolution" -> ResolutionProvider()
            "time" -> TimeProvider()
            "uvCoords" -> UvCoordProvider(program)

            "xyCoord" -> XyPadProvider(uniformPort, showContext)

            "Slider" -> SliderProvider(uniformPort, showContext)
            "ColorPicker" -> ColorPickerProvider(uniformPort, showContext)

            "none" -> null

            else -> throw IllegalArgumentException("unknown type ${name}")
        }
    }


    class ResolutionProvider : DataSourceProvider, DataSource, GlslProgram.ResolutionListener {
        override val supportedTypes: List<String> = listOf("vec2")

        var x = 1f
        var y = 1f

        override fun onResolution(x: Float, y: Float) {
            this.x = x
            this.y = y
        }

        override fun provide(): DataSource = this

        override fun set(uniform: Uniform) = uniform.set(x, y)
    }

    class TimeProvider : DataSourceProvider, DataSource {
        override val supportedTypes: List<String> = listOf("float")

        override fun provide(): DataSource = this

        override fun set(uniform: Uniform) {
            val thisTime = (getTimeMillis() and 0x7ffffff).toFloat() / 1000.0f
            uniform.set(thisTime)
        }
    }

    class UvCoordProvider(
        val program: GlslProgram
    ) : DataSourceProvider, DataSource, GlslRenderer.ArrangementListener {
        override val supportedTypes: List<String> = listOf("sampler2D")

        override fun provide(): DataSource = this

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
        private val uniformPortRef: Patch.UniformPortRef,
        private val showContext: ShowContext
    ) : DataSourceProvider {
        abstract fun createGadget(name: String, config: Map<String, String>): T
        abstract fun set(gadget: T, uniform: Uniform)

        final override fun provide(): DataSource {
            val varName = uniformPortRef.varName
            val gadgetId = "glsl_$varName"
            val displayName = uniformPortRef.name.capitalize()
            val gadget = showContext.getGadget(gadgetId,
                createGadget(displayName, uniformPortRef.pluginConfig))
            return object : DataSource {
                override fun set(uniform: Uniform) {
                    this@GadgetProvider.set(gadget, uniform)
                }
            }
        }
    }

    class SliderProvider(
        uniformPortRef: Patch.UniformPortRef,
        showContext: ShowContext
    ) : GadgetProvider<Slider>(uniformPortRef, showContext) {
        override val supportedTypes: List<String> = listOf("float")

        override fun createGadget(name: String, config: Map<String, String>): Slider =
            Slider(
                name,
                initialValue = config["default"]?.toFloat() ?: 1f,
                minValue = config["min"]?.toFloat() ?: 0f,
                maxValue = config["max"]?.toFloat() ?: 1f,
                stepValue = config["step"]?.toFloat() ?: .01f
                // ,scale = config["scale"] ?: "linear"
            )

        override fun set(gadget: Slider, uniform: Uniform) {
            uniform.set(gadget.value)
        }
    }

    class XyPadProvider(
        uniformPortRef: Patch.UniformPortRef,
        private val showContext: ShowContext
    ) : DataSourceProvider {
        private val varName = uniformPortRef.varName
        private val gadgetIdPrefix = "glsl_$varName"
        private val displayName = uniformPortRef.name.capitalize()

        override val supportedTypes: List<String> = listOf("vec2")

        override fun provide(): DataSource {
            return object : DataSource {
                val xControl = showContext.getGadget("${gadgetIdPrefix}_x", Slider(
                    "$displayName X",
                    initialValue = .5f,
                    minValue = 0f,
                    maxValue = 1f
                ))

                val yControl = showContext.getGadget("${gadgetIdPrefix}_y", Slider(
                    "$displayName Y",
                    initialValue = .5f,
                    minValue = 0f,
                    maxValue = 1f
                ))

                override fun set(uniform: Uniform) {
                    uniform.set(xControl.value, yControl.value)
                }
            }
        }
    }

    class ColorPickerProvider(
        uniformPortRef: Patch.UniformPortRef,
        showContext: ShowContext
    ) : GadgetProvider<ColorPicker>(uniformPortRef, showContext) {
        private val uniformType = uniformPortRef.type

        override val supportedTypes: List<String> = listOf("vec3", "vec4")

        override fun createGadget(name: String, config: Map<String, String>): ColorPicker =
            ColorPicker(
                name,
                initialValue = config["default"]?.let { Color.from(it) } ?: Color.WHITE
            )

        override fun set(gadget: ColorPicker, uniform: Uniform) {
            val color = gadget.color
            when (uniformType) {
                "vec3" -> uniform.set(color.redF, color.greenF, color.blueF)
                "vec4" -> uniform.set(color.redF, color.greenF, color.blueF, color.alphaF)
            }
        }
    }
}