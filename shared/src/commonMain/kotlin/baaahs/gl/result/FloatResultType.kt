package baaahs.gl.result

import baaahs.fixtures.Fixture
import baaahs.gl.GlContext
import baaahs.visualizer.remote.RemoteVisualizers
import com.danielgergely.kgl.GL_RED

// Yuck. XY and XYZ fail, at least on WebGL. Maybe they work on others?
object FloatResultType : FloatsResultType<FloatResultType.ResultBuffer>(
    // Haven't tested this, but I'm assuming it doesn't work.
    1, GL_RED
) {
    override fun createResultBuffer(gl: GlContext, index: Int): ResultBuffer {
        return ResultBuffer(gl, index, this)
    }

    class ResultBuffer(gl: GlContext, index: Int, type: FloatsResultType<ResultBuffer>) : Buffer(gl, index, type) {
        operator fun get(componentIndex: Int): Float {
            val offset = componentIndex * type.stride

            return floatBuffer[offset]
        }

        override fun getFixtureView(fixture: Fixture, bufferOffset: Int): FixtureResults {
            return FloatFixtureResults(this, bufferOffset, fixture.componentCount)
        }
    }

    class FloatFixtureResults(
        private val buffer: ResultBuffer,
        componentOffset: Int,
        componentCount: Int
    ) : FixtureResults(componentOffset, componentCount) {
        operator fun get(componentIndex: Int): Float = buffer[componentOffset + componentIndex]

        override fun send(remoteVisualizers: RemoteVisualizers) = TODO("FloatFixtureResults.send() not implemented")
    }
}