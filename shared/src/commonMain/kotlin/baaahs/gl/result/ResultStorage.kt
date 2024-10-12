package baaahs.gl.result

import baaahs.fixtures.Fixture
import baaahs.gl.GlContext
import baaahs.visualizer.remote.RemoteVisualizers

interface ResultStorage {
    val resultBuffers: List<ResultBuffer>

    fun resize(width: Int, height: Int)
    fun attachTo(fb: GlContext.FrameBuffer)
    fun getFixtureResults(fixture: Fixture, bufferOffset: Int): FixtureResults
    fun release()

    object Empty : ResultStorage {
        override val resultBuffers: List<ResultBuffer>
            get() = emptyList()

        override fun resize(width: Int, height: Int) {
        }

        override fun attachTo(fb: GlContext.FrameBuffer) {
        }

        override fun getFixtureResults(fixture: Fixture, bufferOffset: Int): FixtureResults {
            return object : FixtureResults(bufferOffset, fixture.componentCount) {
                override fun send(remoteVisualizers: RemoteVisualizers) {
                    TODO("not implemented")
                }
            }
        }

        override fun release() {
        }
    }
}