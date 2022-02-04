package baaahs.gl.result

import baaahs.fixtures.Fixture
import baaahs.geom.Vector2F
import baaahs.gl.GlContext
import baaahs.visualizer.remote.RemoteVisualizers
import com.danielgergely.kgl.GL_RGBA

object Vec2ResultType : FloatsResultType<Vec2ResultType.ResultBuffer>(
    // This doesn't work in WebGL2 because... dunno.
    //    2, GL_RG32F, GL_RG
    // readPixels() fails with INVALID_OPERATION.
    // Instead we use four floats and ignore one:
    4, GL_RGBA
) {
    override fun createResultBuffer(gl: GlContext, index: Int): ResultBuffer {
        return ResultBuffer(gl, index, this)
    }

    class ResultBuffer(gl: GlContext, index: Int, type: FloatsResultType<ResultBuffer>) : Buffer(gl, index, type) {
        operator fun get(pixelIndex: Int): Vector2F {
            val offset = pixelIndex * type.stride

            return Vector2F(
                x = floatBuffer[offset],
                y = floatBuffer[offset + 1]
            )
        }

        override fun getFixtureView(fixture: Fixture, bufferOffset: Int): FixtureResults {
            return Vec2FixtureResults(this, bufferOffset, fixture.pixelCount)
        }
    }

    class Vec2FixtureResults(
        private val buffer: ResultBuffer,
        pixelOffset: Int,
        pixelCount: Int
    ) : FixtureResults(pixelOffset, pixelCount) {
        operator fun get(pixelIndex: Int): Vector2F = buffer[pixelOffset + pixelIndex]

        override fun send(remoteVisualizers: RemoteVisualizers) {
            TODO("Vec2FixtureResults.send() not implemented")
        }
    }
}