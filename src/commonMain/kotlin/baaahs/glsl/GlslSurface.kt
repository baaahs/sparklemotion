package baaahs.glsl

import baaahs.Color
import baaahs.Pixels
import baaahs.Surface

class GlslSurface(
    val pixels: SurfacePixels,
    val uniforms: GlslRenderer.Uniforms,
    val uvTranslator: UvTranslator
)

abstract class SurfacePixels(val surface: Surface, val pixel0Index: Int) : Pixels {
    override val size: Int = surface.pixelCount
    override fun set(i: Int, color: Color): Unit = TODO("set not implemented")
    override fun set(colors: Array<Color>): Unit = TODO("set not implemented")
}