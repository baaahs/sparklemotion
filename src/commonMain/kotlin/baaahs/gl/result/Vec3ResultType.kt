package baaahs.gl.result

import baaahs.fixtures.Fixture
import baaahs.geom.Vector3F
import baaahs.gl.GlContext
import baaahs.model.Model
import baaahs.visualizer.remote.RemoteVisualizers
import com.danielgergely.kgl.GL_RGBA

object Vec3ResultType : FloatsResultType<Vec3ResultType.ResultBuffer>(
    // This doesn't work in WebGL2 because EXT_color_buffer_float doesn't have RGB32F!?
    //    3, GlContext.GL_RGB32F, GL_RGB
    // framebufferRenderbuffer() fails with INVALID_ENUM.
    // Instead we use four floats and ignore one:
    4, GlContext.GL_RGBA32F, GL_RGBA
) {
    override fun createResultBuffer(gl: GlContext, index: Int): ResultBuffer {
        return ResultBuffer(gl, index, this)
    }

    class ResultBuffer(gl: GlContext, index: Int, type: ResultType<ResultBuffer>) : Buffer(gl, index, type) {
        operator fun get(pixelIndex: Int): Vector3F {
            val offset = pixelIndex * type.stride

            return Vector3F(
                x = floatBuffer[offset],
                y = floatBuffer[offset + 1],
                z = floatBuffer[offset + 2]
            )
        }

        override fun getFixtureView(fixture: Fixture, bufferOffset: Int): FixtureResults {
            return Vec3FixtureResults(this, bufferOffset, fixture.pixelCount)
        }
    }

    class Vec3FixtureResults(
        private val buffer: ResultBuffer,
        pixelOffset: Int,
        pixelCount: Int
    ) : FixtureResults(pixelOffset, pixelCount) {
        operator fun get(pixelIndex: Int): Vector3F = buffer[pixelOffset + pixelIndex]

        override fun send(entity: Model.Entity?, remoteVisualizers: RemoteVisualizers) {
            TODO("Vec3FixtureResults.send() not implemented")
        }
    }
}