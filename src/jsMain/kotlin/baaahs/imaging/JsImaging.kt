package baaahs.imaging

import baaahs.context2d
import kotlinx.html.dom.create
import kotlinx.html.js.canvas
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLVideoElement
import org.w3c.dom.ImageBitmap
import kotlin.browser.document

actual class NativeBitmap actual constructor(override val width: Int, override val height: Int) : CanvasBitmap(
    createCanvas(width, height)), Bitmap

fun createCanvas(width: Int, height: Int) =
    document.create.canvas {
        this.width = "${width}px"
        this.height = "${height}px"
    }

open class CanvasBitmap(private val canvas: HTMLCanvasElement) : Bitmap {
    override val width = canvas.width
    override val height = canvas.height

    internal val ctx = canvas.context2d()

    override fun drawImage(image: Image) = (image as JsImage).draw(ctx, 0, 0)

    override fun drawImage(
        image: Image,
        sX: Int, sY: Int, sWidth: Int, sHeight: Int,
        dX: Int, dY: Int, dWidth: Int, dHeight: Int
    ) = (image as JsImage).draw(ctx, sX, sY, sWidth, sHeight, dX, dY, dWidth, dHeight)

    override fun copyFrom(other: Bitmap) {
        assertSameSizeAs(other)

        ctx.globalCompositeOperation = "source-over"
        ctx.drawImage((other as CanvasBitmap).canvas, 0.0, 0.0)
    }

    override fun subtract(other: Bitmap) {
        assertSameSizeAs(other)

        ctx.globalCompositeOperation = "difference"
        ctx.drawImage((other as CanvasBitmap).canvas, 0.0, 0.0)
    }

    override fun withData(fn: (data: ByteArray) -> Boolean) {
        val imageData = ctx.getImageData(0.0, 0.0, width.toDouble(), height.toDouble())
        if (fn(imageData.data.asDynamic())) {
            ctx.putImageData(imageData, 0.0, 0.0)
        }
    }

    override fun asImage(): Image {
        return object: JsImage() {
            override val width = this@CanvasBitmap.width
            override val height = this@CanvasBitmap.height
            override fun toBitmap(): Bitmap = this@CanvasBitmap

            override fun draw(ctx: CanvasRenderingContext2D, x: Int, y: Int) {
                ctx.drawImage(canvas, 0.0, 0.0)
            }

            override fun draw(
                ctx: CanvasRenderingContext2D,
                sX: Int, sY: Int, sWidth: Int, sHeight: Int,
                dX: Int, dY: Int, dWidth: Int, dHeight: Int
            ) {
                ctx.drawImage(canvas,
                    sX.toDouble(), sY.toDouble(), sWidth.toDouble(), sHeight.toDouble(),
                    dX.toDouble(), dY.toDouble(), dWidth.toDouble(), dHeight.toDouble())
            }
        }
    }

    private fun assertSameSizeAs(other: Bitmap) {
        if (width != other.width || height != other.height) {
            throw IllegalArgumentException("other bitmap is not the same size" +
                    " (${width}x${height} != ${other.width}x${other.height})")
        }
    }
}

abstract class JsImage : Image {
    abstract fun draw(ctx: CanvasRenderingContext2D, x: Int, y: Int)

    abstract fun draw(ctx: CanvasRenderingContext2D,
        sX: Int, sY: Int, sWidth: Int, sHeight: Int,
        dX: Int, dY: Int, dWidth: Int, dHeight: Int)
}

class ImageBitmapImage(private val imageBitmap: ImageBitmap) : JsImage() {
    override val width = imageBitmap.width
    override val height = imageBitmap.height

    override fun toBitmap(): Bitmap {
        val bitmap = NativeBitmap(imageBitmap.width, imageBitmap.height)
        bitmap.drawImage(this)
        return bitmap
    }

    override fun draw(ctx: CanvasRenderingContext2D, x: Int, y: Int) {
        ctx.drawImage(imageBitmap, 0.0, 0.0)
    }

    override fun draw(ctx: CanvasRenderingContext2D,
        sX: Int, sY: Int, sWidth: Int, sHeight: Int,
        dX: Int, dY: Int, dWidth: Int, dHeight: Int
    ) {
        ctx.drawImage(imageBitmap,
            sX.toDouble(), sY.toDouble(), sWidth.toDouble(), sHeight.toDouble(),
            dX.toDouble(), dY.toDouble(), dWidth.toDouble(), dHeight.toDouble())
    }
}

class VideoElementImage(private val videoEl: HTMLVideoElement) : JsImage() {
    override val width get() = videoEl.videoWidth
    override val height get() = videoEl.videoHeight

    override fun toBitmap(): Bitmap {
        val bitmap = NativeBitmap(videoEl.videoWidth, videoEl.videoHeight)
        bitmap.drawImage(this)
        return bitmap
    }

    override fun draw(ctx: CanvasRenderingContext2D, x: Int, y: Int) {
        ctx.drawImage(videoEl, 0.0, 0.0)
    }

    override fun draw(ctx: CanvasRenderingContext2D,
                      sX: Int, sY: Int, sWidth: Int, sHeight: Int,
                      dX: Int, dY: Int, dWidth: Int, dHeight: Int
    ) {
        ctx.drawImage(videoEl,
            sX.toDouble(), sY.toDouble(), sWidth.toDouble(), sHeight.toDouble(),
            dX.toDouble(), dY.toDouble(), dWidth.toDouble(), dHeight.toDouble())
    }

}