package baaahs.gl.render

import baaahs.fixtures.Fixture
import baaahs.fixtures.ResultBuffer
import baaahs.gl.glsl.GlslProgram
import baaahs.model.ModelInfo

class FixtureRenderPlan(
    val fixture: Fixture,
    val rect0Index: Int,
    val rects: List<Quad.Rect>, // these are in pixels, (0,0) at top left
    val modelInfo: ModelInfo,
    val pixelCount: Int,
    val pixel0Index: Int,
    resultBuffers: List<ResultBuffer>
) {
    var program: GlslProgram? = null

    val resultViews = resultBuffers.map { it.getView(pixel0Index, pixelCount) }

    fun useProgram(program: GlslProgram?) {
        if (program != null && this.program != null)
            throw IllegalStateException("buffer already bound")
        this.program = program
    }

    fun sendFrame() {
        fixture.transport.send(fixture, resultViews)
    }

    fun release() {
        useProgram(null)
    }
}