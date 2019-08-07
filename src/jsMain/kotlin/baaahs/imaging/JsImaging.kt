package baaahs.imaging

import baaahs.MediaDevices
import baaahs.context2d
import baaahs.first
import kotlinx.html.dom.create
import kotlinx.html.js.canvas
import org.khronos.webgl.Uint8ClampedArray
import org.khronos.webgl.get
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLVideoElement
import org.w3c.dom.ImageBitmap
import kotlin.browser.document

actual class NativeBitmap actual constructor(override val width: Int, override val height: Int) :
    CanvasBitmap(createCanvas(width, height)), Bitmap {
}

fun createCanvas(width: Int, height: Int) =
    document.create.canvas {
        this.width = "${width}px"
        this.height = "${height}px"
    }

open class CanvasBitmap(internal val canvas: HTMLCanvasElement) : Bitmap {
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

        ctx.resetTransform()
        ctx.globalCompositeOperation = "source-over"
        ctx.drawImage((other as CanvasBitmap).canvas, 0.0, 0.0)
        ctx.resetTransform()
    }

    private fun apply(other: Bitmap, operation: String) {
        other as CanvasBitmap
        assertSameSizeAs(other)

        ctx.resetTransform()
        ctx.globalCompositeOperation = operation
        ctx.drawImage(other.canvas, 0.0, 0.0)
        ctx.resetTransform()
    }

    override fun lighten(other: Bitmap) {
        apply(other, "lighten")
    }

    override fun darken(other: Bitmap) {
        apply(other, "darken")
    }

    override fun subtract(other: Bitmap) {
        apply(other, "difference")
    }

    override fun toDataUrl(): String = canvas.toDataURL("image/webp")

    override fun withData(region: MediaDevices.Region, fn: (data: UByteClampedArray) -> Boolean) {
        val x = region.x0.toDouble()
        val y = region.y0.toDouble()
        val width = region.width.toDouble()
        val height = region.height.toDouble()
        val imageData = ctx.getImageData(x, y, width, height)
        if (fn(JsUByteClampedArray(imageData.data))) {
            ctx.putImageData(imageData, x, y, x, y, width, height)
        }
    }

    override fun asImage(): Image {
        return object : JsImage() {
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
                ctx.drawImage(
                    canvas,
                    sX.toDouble(), sY.toDouble(), sWidth.toDouble(), sHeight.toDouble(),
                    dX.toDouble(), dY.toDouble(), dWidth.toDouble(), dHeight.toDouble()
                )
            }
        }
    }

    override fun clone(): Bitmap {
        val newCanvas = document.createElement("canvas") as HTMLCanvasElement
        newCanvas.width = canvas.width
        newCanvas.height = canvas.height
        val ctx = newCanvas.getContext("2d") as CanvasRenderingContext2D
        ctx.drawImage(canvas, 0.0, 0.0)
        return CanvasBitmap(newCanvas)
    }

    private fun assertSameSizeAs(other: Bitmap) {
        if (width != other.width || height != other.height) {
            throw IllegalArgumentException(
                "other bitmap is not the same size" +
                        " (${width}x${height} != ${other.width}x${other.height})"
            )
        }
    }
}

abstract class JsImage : Image {
    abstract fun draw(ctx: CanvasRenderingContext2D, x: Int, y: Int)

    abstract fun draw(
        ctx: CanvasRenderingContext2D,
        sX: Int, sY: Int, sWidth: Int, sHeight: Int,
        dX: Int, dY: Int, dWidth: Int, dHeight: Int
    )
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

    override fun draw(
        ctx: CanvasRenderingContext2D,
        sX: Int, sY: Int, sWidth: Int, sHeight: Int,
        dX: Int, dY: Int, dWidth: Int, dHeight: Int
    ) {
        ctx.drawImage(
            imageBitmap,
            sX.toDouble(), sY.toDouble(), sWidth.toDouble(), sHeight.toDouble(),
            dX.toDouble(), dY.toDouble(), dWidth.toDouble(), dHeight.toDouble()
        )
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

    override fun draw(
        ctx: CanvasRenderingContext2D,
        sX: Int, sY: Int, sWidth: Int, sHeight: Int,
        dX: Int, dY: Int, dWidth: Int, dHeight: Int
    ) {
        ctx.drawImage(
            videoEl,
            sX.toDouble(), sY.toDouble(), sWidth.toDouble(), sHeight.toDouble(),
            dX.toDouble(), dY.toDouble(), dWidth.toDouble(), dHeight.toDouble()
        )
    }
}

class JsUByteClampedArray(val delegate: Uint8ClampedArray) : UByteClampedArray {
    override val size: Int get() = delegate.length

    override operator fun get(index: Int): Int {
        return delegate[index].toInt()
    }

    override operator fun set(index: Int, value: UByte) {
        delegate.asDynamic()[index] = value
    }
}