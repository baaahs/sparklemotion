package baaahs.gl.render

import baaahs.fixtures.Fixture
import baaahs.fixtures.FixtureResults
import baaahs.fixtures.ResultBuffer
import baaahs.gl.GlContext
import baaahs.model.Model
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
            return object : FixtureResults(bufferOffset, fixture.pixelCount) {
                override fun send(entity: Model.Entity?, remoteVisualizers: RemoteVisualizers) {
                    TODO("not implemented")
                }
            }
        }

        override fun release() {
        }
    }
}