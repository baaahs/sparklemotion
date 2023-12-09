package baaahs.imaging

import baaahs.decodeBase64
import baaahs.document
import baaahs.get2DContext
import baaahs.util.Clock
import baaahs.util.JsClock
import external.gifuct.ParsedFrameDims
import external.gifuct.decompressFrames
import external.gifuct.parseGIF
import js.typedarrays.Uint8Array
import js.typedarrays.Uint8ClampedArray
import kotlinx.coroutines.await
import org.khronos.webgl.get
import web.canvas.CanvasRenderingContext2D
import web.canvas.ImageBitmap
import web.canvas.ImageData
import web.gl.WebGLRenderingContext
import web.html.HTMLCanvasElement
import web.html.HTMLImageElement
import web.html.HTMLVideoElement

actual fun imageFromDataUrl(dataUrl: String): Image {
    return if (dataUrl.looksLikeGif()) {
        GifImage(decodeBase64(dataUrl.substringAfter(",")))
    } else {
        val image = web.html.Image()
        image.src = dataUrl
        return DomImage(image)
    }
}

actual fun createWritableBitmap(width: Int, height: Int): Bitmap =
    CanvasBitmap(width, height)

fun createCanvas(width: Int, height: Int) =
    (document.createElement("canvas") as HTMLCanvasElement).apply {
        this.width = width
        this.height = height
    }

abstract class JsImage : Image {
    abstract fun draw(ctx: CanvasRenderingContext2D, x: Int, y: Int)

    abstract fun draw(
        ctx: CanvasRenderingContext2D,
        sX: Int, sY: Int, sWidth: Int, sHeight: Int,
        dX: Int, dY: Int, dWidth: Int, dHeight: Int
    )
}

class DomImage(val image: web.html.Image) : JsImage() {
    override val width: Int
        get() = image.width.toInt()
    override val height: Int
        get() = image.height.toInt()

    override fun draw(ctx: CanvasRenderingContext2D, x: Int, y: Int) {
        ctx.drawImage(image, x.toDouble(), y.toDouble())
    }

    override fun draw(
        ctx: CanvasRenderingContext2D,
        sX: Int,
        sY: Int,
        sWidth: Int,
        sHeight: Int,
        dX: Int,
        dY: Int,
        dWidth: Int,
        dHeight: Int
    ) {
        ctx.drawImage(
            image,
            sX.toDouble(), sY.toDouble(), sWidth.toDouble(), sHeight.toDouble(),
            dX.toDouble(), dY.toDouble(), dWidth.toDouble(), dHeight.toDouble()
        )

    }

    override fun toBitmap(): Bitmap {
        val bitmap = createWritableBitmap(width, height)
        bitmap.drawImage(this)
        return bitmap
    }
}

class WebGlImage(private val webGlContext: WebGLRenderingContext) : JsImage() {
    override val width: Int
        get() = webGlContext.drawingBufferWidth.toInt()
    override val height: Int
        get() = webGlContext.drawingBufferHeight.toInt()
    private val canvas: HTMLCanvasElement
        get() = webGlContext.canvas as HTMLCanvasElement

    override fun toBitmap(): Bitmap {
        val newCanvas = createCanvas(width, height)
        newCanvas.get2DContext().drawImage(canvas, 0.0, 0.0)
        return CanvasBitmap(newCanvas)
    }

    override fun draw(ctx: CanvasRenderingContext2D, x: Int, y: Int) {
        ctx.drawImage(canvas, x.toDouble(), y.toDouble())
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

class ImageBitmapImage(private val imageBitmap: ImageBitmap) : JsImage() {
    override val width = imageBitmap.width
    override val height = imageBitmap.height

    override fun toBitmap(): Bitmap {
        val bitmap = createWritableBitmap(imageBitmap.width, imageBitmap.height)
        bitmap.drawImage(this)
        return bitmap
    }

    override fun draw(ctx: CanvasRenderingContext2D, x: Int, y: Int) {
        ctx.drawImage(imageBitmap, x.toDouble(), y.toDouble())
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

    companion object {
        suspend fun fromImg(image: HTMLImageElement) =
            ImageBitmapImage(
                kotlinx.browser.window.createImageBitmap(
                    image.unsafeCast<org.w3c.dom.ImageBitmapSource>()
                ).await()
                    .unsafeCast<ImageBitmap>()
            )
    }
}

class VideoElementImage(private val videoEl: HTMLVideoElement) : JsImage() {
    override val width get() = videoEl.videoWidth
    override val height get() = videoEl.videoHeight

    private var haveNewFrame = true

    init {
        videoEl.requestVideoFrameCallback { _, _ ->
            haveNewFrame = true
        }
    }

    override fun toBitmap(): Bitmap {
        val bitmap = createWritableBitmap(width, height)
        bitmap.drawImage(this)
        haveNewFrame = false
        return bitmap
    }

    override fun hasChanged(): Boolean = haveNewFrame

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

class JsUByteClampedArray(private val delegate: Uint8ClampedArray) : UByteClampedArray {
    override val size: Int get() = delegate.length

    override operator fun get(index: Int): Int {
        return delegate[index].toInt()
    }

    override operator fun set(index: Int, value: UByte) {
        delegate.asDynamic()[index] = value
    }
}

class GifImage(data: ByteArray, clock: Clock = JsClock) : Image {
    private val parsedGif = parseGIF(Uint8Array.asDynamic().from(data))
    private val frames = decompressFrames(parsedGif, true)
    private val animator = Animator(frames.map { it.delay }, clock)
    private val imageDatas: List<ImageData>

    init {
        val bytes = Uint8ClampedArray(width * height * 4)
        var needsDisposal: ParsedFrameDims? = null
        val fullWidth = parsedGif.lsd.width

        imageDatas = buildList {
            frames.forEach { frame ->
                needsDisposal?.let { dims ->
                    val clear = arrayOf<Byte>(0, 0, 0, 0)
                    with(dims) {
                        for (y in top until top + height) {
                            for (x in left until left + width) {
                                bytes.set(clear, (y * fullWidth + x) * 4)
                            }
                        }
                    }
                    needsDisposal = null
                }

                with(frame.dims) {
                    for (y in 0 until height) {
                        for (x in 0 until width) {
                            val srcOffset = offset(x, y, frame.dims.width)
                            val destOffset = offset(left + x, top + y, fullWidth)

                            val bytesBuf = bytes.asDynamic()
                            bytesBuf[destOffset + 0] = frame.patch[srcOffset + 0]
                            bytesBuf[destOffset + 1] = frame.patch[srcOffset + 1]
                            bytesBuf[destOffset + 2] = frame.patch[srcOffset + 2]
                            bytesBuf[destOffset + 3] = frame.patch[srcOffset + 3]
                        }
                    }
                }

                add(ImageData(Uint8ClampedArray(bytes), width, height))
            }
        }
    }

    override val width: Int
        get() = parsedGif.lsd.width
    override val height: Int
        get() = parsedGif.lsd.width

    private var recentFrame: Int = -1

    override fun toBitmap(): Bitmap {
        val frameIndex = animator.getCurrentFrame()
        recentFrame = frameIndex

        return CanvasBitmap(width, height).apply {
            canvas.get2DContext().putImageData(imageDatas[frameIndex], 0.0, 0.0)
        }
    }

    override fun hasChanged(): Boolean {
        val currentFrame = animator.getCurrentFrame()
        return currentFrame != recentFrame
    }

    private fun offset(x: Int, y: Int, width: Int) =
        (y * width + x) * 4
}