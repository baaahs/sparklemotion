package baaahs.shaders

import baaahs.Color
import baaahs.IdentifiedSurface
import baaahs.Surface
import baaahs.glsl.LinearSurfacePixelStrategy
import baaahs.glsl.PanelSpaceUvTranslator
import kotlin.math.sqrt

/**
 * A shader that sets all pixels to a single color within x/y constraints.
 */
class SimpleSpatialShader() : Shader<SimpleSpatialShader.Buffer>(ShaderId.SIMPLE_SPATIAL) {
    override fun createBuffer(surface: Surface): Buffer = Buffer()

    override fun createRenderer(surface: Surface): Renderer = Renderer(surface)

    inner class Buffer : Shader.Buffer {
        override val shader: Shader<*>
            get() = this@SimpleSpatialShader

        var color: Color = Color.WHITE
        var centerX: Float = 0.5f
        var centerY: Float = 0.5f
        var radius: Float = 0.75f
    }

    class Renderer(surface: Surface) : Shader.Renderer<Buffer> {
        private val uvTranslator =
            if (surface is IdentifiedSurface) {
                PanelSpaceUvTranslator.forPixels(LinearSurfacePixelStrategy.forSurface(surface))
            } else null

        override fun draw(buffer: Buffer, pixelIndex: Int): Color {
            if (uvTranslator == null) return Color.BLACK

            val (pixX, pixY) = uvTranslator.getUV(pixelIndex)

            val distX = pixX - buffer.centerX
            val distY = pixY - buffer.centerY
            val dist = sqrt(distX * distX + distY * distY)
            return when {
                dist < buffer.radius - 0.025f -> buffer.color
                dist < buffer.radius + 0.025f -> Color.BLACK
                else -> buffer.color.fade(Color.BLACK, dist * 2)
            }
        }
    }
}
