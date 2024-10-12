package baaahs.gl.result

import baaahs.fixtures.Fixture
import baaahs.gl.GlContext

class SingleResultStorage(private val resultBuffer: ResultBuffer) : ResultStorage {
    override val resultBuffers: List<ResultBuffer>
        get() = listOf(resultBuffer)

    override fun resize(width: Int, height: Int) {
        resultBuffer.resize(width, height)
    }

    override fun attachTo(fb: GlContext.FrameBuffer) {
        resultBuffer.attachTo(fb)
    }

    override fun getFixtureResults(fixture: Fixture, bufferOffset: Int) =
        resultBuffer.getFixtureView(fixture, bufferOffset)

    override fun release() {
        resultBuffer.release()
    }
}