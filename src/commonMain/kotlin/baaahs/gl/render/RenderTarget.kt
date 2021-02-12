package baaahs.gl.render

import baaahs.fixtures.Fixture
import baaahs.fixtures.ResultBuffer
import baaahs.gl.glsl.GlslProgram
import baaahs.model.ModelInfo

interface RenderTarget {
    val fixture: Fixture
    val modelInfo: ModelInfo
    val pixelCount: Int

    fun sendFrame()
    fun release()
}

class FixtureRenderTarget(
    override val fixture: Fixture,
    val rect0Index: Int,
    val rects: List<Quad.Rect>, // these are in pixels, (0,0) at top left
    override val modelInfo: ModelInfo,
    override val pixelCount: Int,
    val pixel0Index: Int,
    resultBuffers: List<ResultBuffer>
) : RenderTarget {
    var program: GlslProgram? = null
        private set

    val resultViews = resultBuffers.map { it.getView(pixel0Index, pixelCount) }

    override fun sendFrame() {
        fixture.transport.send(fixture, resultViews)
    }

    override fun release() {
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