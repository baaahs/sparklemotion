package baaahs.glshaders

import baaahs.getTimeMillis
import baaahs.glsl.GlslRenderer
import baaahs.glsl.Uniform
import com.danielgergely.kgl.*

class CorePlugin : Plugin {
    override val name: String = "SparkleMotion Core"

    override fun matchUniformProvider(type: String, name: String, program: GlslProgram): GlslProgram.UniformProvider? {
        return when (name) {
            "resolution" -> ResolutionProvider()
            "time" -> TimeProvider()
            "uvCoords" -> UvCoordProvider(program)
            else -> null
        }
    }


    class ResolutionProvider : GlslProgram.UniformProvider, GlslProgram.ResolutionListener {
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
        override fun set(uniform: Uniform) {
            val thisTime = (getTimeMillis() and 0x7ffffff).toFloat() / 1000.0f
            uniform.set(thisTime)
        }
    }

    class UvCoordProvider(val program: GlslProgram) : GlslProgram.UniformProvider, GlslRenderer.ArrangementListener {
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
}