package baaahs.gl.render

import baaahs.Color
import baaahs.Pixels
import baaahs.ShowRunner
import baaahs.fixtures.Fixture
import baaahs.gl.glsl.GlslProgram
import baaahs.model.ModelInfo
import com.danielgergely.kgl.ByteBuffer

class FixtureRenderPlan(
    val fixture: Fixture,
    val renderResult: RenderResult,
    val rect0Index: Int,
    val rects: List<Quad.Rect>, // these are in pixels, (0,0) at top left
    val modelInfo: ModelInfo
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

class FixturePixels(
    private val renderEngine: RenderEngine,
    override val size: Int,
    override val bufferOffset: Int
) : Pixels, RenderResult {
    override fun get(i: Int): Color {
        val pixelBuffer = renderEngine.arrangement.resultBuffer as ByteBuffer
        val offset = (bufferOffset + i) * 4
        return Color(
            red = pixelBuffer[offset],
            green = pixelBuffer[offset + 1],
            blue = pixelBuffer[offset + 2],
            alpha = pixelBuffer[offset + 3]
        )
    }

    override fun set(i: Int, color: Color): Unit = TODO("set not implemented")
    override fun set(colors: Array<Color>): Unit = TODO("set not implemented")
}

interface RenderResult {
    val size: Int
    val bufferOffset: Int
}