package baaahs.gl.result

import baaahs.fixtures.Fixture
import baaahs.geom.Vector4F
import baaahs.gl.GlContext
import baaahs.visualizer.remote.RemoteVisualizers
import com.danielgergely.kgl.GL_RGBA

object Vec4ResultType : FloatsResultType<Vec4ResultType.ResultBuffer>(4, GL_RGBA) {
    override fun createResultBuffer(gl: GlContext, index: Int): ResultBuffer {
        return ResultBuffer(gl, index, this)
    }

    class ResultBuffer(gl: GlContext, index: Int, type: FloatsResultType<ResultBuffer>) : Buffer(gl, index, type) {
        operator fun get(componentIndex: Int): Vector4F {
            val offset = componentIndex * type.stride

            return Vector4F(
                x = floatBuffer[offset],
                y = floatBuffer[offset + 1],
                z = floatBuffer[offset + 2],
                w = floatBuffer[offset + 3]
            )
        }

        override fun getFixtureView(fixture: Fixture, bufferOffset: Int): FixtureResults {
            return Vec4FixtureResults(this, bufferOffset, fixture.componentCount)
        }
    }

    class Vec4FixtureResults(
        private val buffer: ResultBuffer,
        componentOffset: Int,
        componentCount: Int
    ) : FixtureResults(componentOffset, componentCount) {
        operator fun get(componentIndex: Int): Vector4F = buffer[componentOffset + componentIndex]

        override fun send(remoteVisualizers: RemoteVisualizers) {
            TODO("Vec4FixtureResults.send() not implemented")
        }
    }
}