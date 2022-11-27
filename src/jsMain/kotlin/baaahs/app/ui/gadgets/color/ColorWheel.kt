package baaahs.app.ui.gadgets.color

import baaahs.Color
import baaahs.geom.Vector2F
import canvas.CanvasRenderingContext2D
import dom.html.HTMLCanvasElement
import dom.html.RenderingContextId
import org.khronos.webgl.set
import kotlin.math.floor
import kotlin.math.max

enum class HarmonyMode {
    custom,
    triad,
    analogous,
}

private class Bitmap(
    private val ctx: CanvasRenderingContext2D,
    private val width: Int,
    height: Int
) {
    private val image = ctx.createImageData(
        max(width.toDouble(), 1.0),
        max(height.toDouble(), 1.0)
    )
    private val data = image.data

    fun setPixel(x: Int, y: Int, color: Color) {
        val index = (x + y * width) * pixelWidth

        data[index] = color.redI.asDynamic() as Byte
        data[index + 1] = color.greenI.asDynamic() as Byte
        data[index + 2] = color.blueI.asDynamic() as Byte
        data[index + 3] = color.alphaI.asDynamic() as Byte
    }

    fun draw() {
        ctx.putImageData(image, 0.0, 0.0)
    }

    companion object {
        private const val pixelWidth = 4 // each pixel requires 4 slots in the data array
    }
}

class ColorWheel(
    canvasEl: HTMLCanvasElement,
    var radius: Int,
    var harmonyMode: HarmonyMode = HarmonyMode.triad,
    var colors: Array<Color> = Array(3) { Color.WHITE }
) {
    private val ctx = canvasEl.getContext(RenderingContextId.canvas) as CanvasRenderingContext2D

    private var priorRadius: Int? = null

    fun drawWheel() {
        if (radius == priorRadius) return
        priorRadius = radius

        val width = radius * 2
        val height = radius * 2
        val featheringPx = 1 // border between color wheel and background

        val bitmap = Bitmap(ctx, width, height)

        for (x in -radius until radius) {
            for (y in -radius until radius) {
                val xy = Vector2F(x.toFloat(), y.toFloat())
                val polar = xy.toPolar()

                val alpha = if (polar.r >= radius - featheringPx) {
                    max(1f - (polar.r - (radius - featheringPx)) / featheringPx, 0f)
                } else 1f

                val color = polar.copy(r = polar.r / radius).toColor(alpha)

                bitmap.setPixel(x + radius, y + radius, color)
            }
        }

        bitmap.draw()
    }

    /**
     * @param xy Coordinate within color wheel, centered at (0,0).
     */
    fun getUpdatedColors(xy: Vector2F, index: Int): Array<Color> {
        val color = (xy / radius.toFloat()).toPolar().toColor()

        val updatedColors = colors.copyOf()
        updatedColors[index] = color

        if (harmonyMode === HarmonyMode.triad) {
            for (i in 1 until colors.size) {
                val nextIndex = (index + i) % colors.size
                val hsb = colors[index].toHSB()
                val nextHue = hsb.hue + (((360 / colors.size) * i) % 360)
                val nextColor = hsb.withHue(nextHue).toRGB()
                updatedColors[nextIndex] = nextColor
            }
        } else if (harmonyMode === HarmonyMode.analogous) {
            val analogousHueSpread = 90
            val spreadStep = analogousHueSpread / colors.size
            for (i in 1 until colors.size) {
                val nextIndex = (index + i) % colors.size
                val hsb = colors[index].toHSB()
                var indexOffset = i + floor(colors.size / 2.0)
                if (i >= colors.size / 2) indexOffset += 1
                val nextHue =
                    (hsb.hue - analogousHueSpread + spreadStep * indexOffset) % 360
                val nextColor = hsb.withHue(nextHue.toFloat()).toRGB()
                updatedColors[nextIndex] = nextColor
            }
        }

        return updatedColors
    }
}