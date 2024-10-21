package baaahs.imaging

import baaahs.MediaDevices
import com.danielgergely.kgl.ByteBuffer

/**
 * An immutable, but possibly changing over time, image source.
 *
 * E.g., this could be a video stream or an animated GIF.
 */
interface Image {
    val width: Int
    val height: Int
    val dimen: Dimen get() = Dimen(width, height)

    fun toBitmap(): Bitmap
    fun hasChanged(): Boolean = true

    companion object {
        fun fromDataUrl(dataUrl: String) = imageFromDataUrl(dataUrl)
    }
}

/** A mutable, static image. */
interface Bitmap {
    val width: Int
    val height: Int
    val dimen: Dimen get() = Dimen(width, height)

    fun drawImage(image: Image)

    fun drawImage(
        image: Image,
        sX: Int, sY: Int, sWidth: Int, sHeight: Int,
        dX: Int, dY: Int, dWidth: Int, dHeight: Int
    )

    fun copyFrom(other: Bitmap): Bitmap

    fun lighten(other: Bitmap): Bitmap

    fun darken(other: Bitmap): Bitmap

    fun subtract(other: Bitmap): Bitmap

    fun multiply(other: Bitmap): Bitmap

    fun withData(
        region: MediaDevices.Region = MediaDevices.Region.containing(this),
        fn: (data: UByteClampedArray) -> Boolean
    )

    fun withGlBuffer(
        region: MediaDevices.Region = MediaDevices.Region.containing(this),
        fn: (data: ByteBuffer) -> Unit
    )

    fun asImage(): Image
    fun toDataUrl(): String

    fun clone(): Bitmap
}

interface UByteClampedArray {
    val size: Int
    operator fun get(index: Int): Int
    operator fun set(index: Int, value: UByte)
}

expect fun imageFromDataUrl(dataUrl: String): Image

expect fun createWritableBitmap(width: Int, height: Int): Bitmap

private val startsLikeGif = Regex("^data:image/[a-z]+;base64,R0lG")
internal fun String.looksLikeGif(): Boolean {
    return startsLikeGif.matchesAt(this, 0)
}
