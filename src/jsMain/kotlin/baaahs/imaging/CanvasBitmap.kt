package baaahs.imaging

import baaahs.MediaDevices
import baaahs.get2DContext
import com.danielgergely.kgl.ByteBuffer
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import web.canvas.CanvasRenderingContext2D
import web.canvas.GlobalCompositeOperation
import web.html.HTMLCanvasElement

open class CanvasBitmap(internal val canvas: HTMLCanvasElement) : Bitmap {
    constructor(width: Int, height: Int): this(createCanvas(width, height))

    override val width = canvas.width
    override val height = canvas.height

    internal val ctx = canvas.get2DContext()

    override fun drawImage(image: Image) {
        (image as JsImage).draw(ctx, 0, 0)
    }

    override fun drawImage(
        image: Image,
        sX: Int, sY: Int, sWidth: Int, sHeight: Int,
        dX: Int, dY: Int, dWidth: Int, dHeight: Int
    ) {
        ctx.resetTransform()
        (image as JsImage).draw(ctx, sX, sY, sWidth, sHeight, dX, dY, dWidth, dHeight)
    }

    private fun apply(other: Bitmap, operation: GlobalCompositeOperation) {
        other as CanvasBitmap
        assertSameSizeAs(other)

        ctx.resetTransform()
        ctx.globalCompositeOperation = operation
        ctx.drawImage(other.canvas, 0.0, 0.0)
        ctx.resetTransform()
    }

    override fun copyFrom(other: Bitmap): Bitmap {
        apply(other, GlobalCompositeOperation.sourceOver)
        return this
    }

    override fun lighten(other: Bitmap): Bitmap {
        apply(other, GlobalCompositeOperation.lighten)
        return this
    }

    override fun darken(other: Bitmap): Bitmap {
        apply(other, GlobalCompositeOperation.darken)
        return this
    }

    override fun subtract(other: Bitmap): Bitmap {
        apply(other, GlobalCompositeOperation.difference)
        return this
    }

    override fun multiply(other: Bitmap): Bitmap {
        apply(other, GlobalCompositeOperation.multiply)
        return this
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

    override fun withGlBuffer(region: MediaDevices.Region, fn: (data: ByteBuffer) -> Unit) {
        val x = region.x0.toDouble()
        val y = region.y0.toDouble()
        val width = region.width.toDouble()
        val height = region.height.toDouble()
        val imageData = ctx.getImageData(x, y, width, height)
        fn(ByteBuffer(Uint8Array(imageData.data.buffer as ArrayBuffer)))
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
        val newCanvas = createCanvas(canvas.width, canvas.height)
        val ctx = newCanvas.get2DContext()
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