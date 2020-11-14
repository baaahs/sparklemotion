package baaahs.gl.render

import baaahs.fixtures.Fixture
import baaahs.fixtures.ResultBuffer
import baaahs.gl.glsl.GlslProgram
import baaahs.model.ModelInfo

class RenderTarget(
    val fixture: Fixture,
    val rect0Index: Int,
    val rects: List<Quad.Rect>, // these are in pixels, (0,0) at top left
    val modelInfo: ModelInfo,
    val pixelCount: Int,
    val pixel0Index: Int,
    resultBuffers: List<ResultBuffer>
) {
    var program: GlslProgram? = null
        private set

    val resultViews = resultBuffers.map { it.getView(pixel0Index, pixelCount) }

    fun sendFrame() {
        fixture.transport.send(fixture, resultViews)
    }

    fun release() {
        program = null
    }

    /** Only call me from [RenderEngine]! */
    internal fun usingProgram(program: GlslProgram?) {
        this.program = program
    }

    override fun toString(): String {
        return "RenderTarget(fixture=$fixture, rect0Index=$rect0Index, rects=$rects, pixel0Index=$pixel0Index)"
    }
}