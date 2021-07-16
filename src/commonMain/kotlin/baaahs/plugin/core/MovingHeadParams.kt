package baaahs.plugin.core

import baaahs.fixtures.FloatsResultType
import baaahs.fixtures.ResultType
import baaahs.gl.GlContext
import baaahs.gl.glsl.GlslExpr
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import com.danielgergely.kgl.GL_RGBA

data class MovingHeadParams(
    val pan: Float,
    val tilt: Float,
    val colorWheel: Float,
    val dimmer: Float
) {

    companion object {
        val struct = GlslType.Struct(
            "MovingHeadParams",
            "pan" to GlslType.Float,
            "tilt" to GlslType.Float,
            "colorWheel" to GlslType.Float,
            "dimmer" to GlslType.Float,
            defaultInitializer = GlslExpr("MovingHeadParams(0., 0., 0., 1.)")
        )

        val contentType = ContentType(
            "moving-head-params", "Moving Head Params",
            struct, outputRepresentation = GlslType.Vec4
        )

        val resultType: ResultType = object : FloatsResultType(4, GlContext.GL_RGBA32F, GL_RGBA) {
            override fun createResultBuffer(gl: GlContext, index: Int) = ResultBuffer(gl, index, this)
        }
    }

    class ResultBuffer(gl: GlContext, index: Int, type: ResultType) : FloatsResultType.Buffer(gl, index, type) {
        operator fun get(pixelIndex: Int): MovingHeadParams {
            val offset = pixelIndex * type.stride

            return MovingHeadParams(
                pan = floatBuffer[offset],
                tilt = floatBuffer[offset + 1],
                colorWheel = floatBuffer[offset + 2],
                dimmer = floatBuffer[offset + 3]
            )
        }

        override fun getView(pixelOffset: Int, pixelCount: Int): baaahs.fixtures.ResultView {
            return ResultView(this, pixelOffset, pixelCount)
        }
    }

    class ResultView(
        val buffer: ResultBuffer,
        pixelOffset: Int,
        pixelCount: Int
    ) : baaahs.fixtures.ResultView(pixelOffset, pixelCount) {
        operator fun get(pixelIndex: Int): MovingHeadParams = buffer[pixelOffset + pixelIndex]
    }
}