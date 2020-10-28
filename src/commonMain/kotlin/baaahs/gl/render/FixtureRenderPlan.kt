package baaahs.gl.render

import baaahs.ShowRunner
import baaahs.fixtures.DeviceParamBuffer
import baaahs.fixtures.Fixture
import baaahs.gl.glsl.GlslProgram
import baaahs.model.ModelInfo

class FixtureRenderPlan(
    val fixture: Fixture,
    val rect0Index: Int,
    val rects: List<Quad.Rect>, // these are in pixels, (0,0) at top left
    val modelInfo: ModelInfo,
    val pixelCount: Int,
    val pixel0Index: Int,
    val resultBuffers: List<DeviceParamBuffer>
) {
    var program: GlslProgram? = null
    val receivers = mutableListOf<ShowRunner.FixtureReceiver>()

    fun useProgram(program: GlslProgram?) {
        if (program != null && this.program != null)
            throw IllegalStateException("buffer already bound")
        this.program = program
    }

    fun release() {
        useProgram(null)
    }
}