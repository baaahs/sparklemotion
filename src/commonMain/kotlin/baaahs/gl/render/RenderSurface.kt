package baaahs.gl.render

import baaahs.Color
import baaahs.Pixels
import baaahs.ShowRunner
import baaahs.Surface
import baaahs.gl.glsl.GlslProgram
import baaahs.model.ModelInfo

class RenderSurface(
    val pixels: SurfacePixels,
    val rect0Index: Int,
    val rects: List<Quad.Rect>, // these are in pixels, (0,0) at top left
    val modelInfo: ModelInfo
) {
    var program: GlslProgram? = null
    val receivers = mutableListOf<ShowRunner.SurfaceReceiver>()

    fun useProgram(program: GlslProgram?) {
        if (program != null && this.program != null)
            throw IllegalStateException("buffer already bound")
        this.program = program
    }

    fun release() {
        useProgram(null)
    }
}

abstract class SurfacePixels(val surface: Surface, val pixel0Index: Int) : Pixels {
    override val size: Int = surface.pixelCount
    override fun set(i: Int, color: Color): Unit = TODO("set not implemented")
    override fun set(colors: Array<Color>): Unit = TODO("set not implemented")
}