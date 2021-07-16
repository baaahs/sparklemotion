package baaahs.visualizer

import baaahs.fixtures.IResultBuffer
import baaahs.fixtures.ResultView
import baaahs.gl.GlContext
import baaahs.gl.render.FixtureRenderTarget

actual class DirectResultBuffer actual constructor(gl: GlContext, resultIndex: Int) : IResultBuffer {
    override fun resize(width: Int, height: Int, renderTargets: List<FixtureRenderTarget>) {
        TODO("not implemented")
    }

    override fun attachTo(fb: GlContext.FrameBuffer) {
        TODO("not implemented")
    }

    override fun afterFrame(frameBuffer: GlContext.FrameBuffer) {
        TODO("not implemented")
    }

    override fun getView(pixelOffset: Int, pixelCount: Int): ResultView {
        TODO("not implemented")
    }

    override fun release() {
        TODO("not implemented")
    }
}