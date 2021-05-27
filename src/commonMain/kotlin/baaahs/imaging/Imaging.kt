package baaahs.imaging

import baaahs.MediaDevices

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

    fun copyFrom(other: Bitmap)

    fun lighten(other: Bitmap)

    fun darken(other: Bitmap)

    fun subtract(other: Bitmap)

    fun withData(
        region: MediaDevices.Region = MediaDevices.Region.containing(this),
        fn: (data: UByteClampedArray) -> Boolean
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

expect class NativeBitmap(width: Int, height: Int) : Bitmap