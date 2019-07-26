package baaahs.imaging

import baaahs.MediaDevices

actual class NativeBitmap actual constructor(width: Int, height: Int) : Bitmap {
    override fun toDataUrl(): String {
        TODO("NativeBitmap.toDataUrl not implemented")
    }

    override val width: Int
        get() = TODO("NativeBitmap.width not implemented")

    override val height: Int
        get() = TODO("NativeBitmap.height not implemented")

    override fun drawImage(image: Image) {
        TODO("NativeBitmap.drawImage not implemented")
    }

    override fun drawImage(
        image: Image,
        sX: Int,
        sY: Int,
        sWidth: Int,
        sHeight: Int,
        dX: Int,
        dY: Int,
        dWidth: Int,
        dHeight: Int
    ) {
        TODO("NativeBitmap.drawImage not implemented")
    }
    override fun copyFrom(other: Bitmap) {
        TODO("NativeBitmap.copyFrom not implemented")
    }

    override fun lighten(other: Bitmap) {
        TODO("NativeBitmap.lighten not implemented")
    }

    override fun darken(other: Bitmap) {
        TODO("NativeBitmap.darken not implemented")
    }

    override fun subtract(other: Bitmap) {
        TODO("NativeBitmap.subtract not implemented")
    }

    override fun withData(region: MediaDevices.Region, fn: (data: UByteClampedArray) -> Boolean) {
        TODO("NativeBitmap.withData not implemented")
    }

    override fun asImage(): Image {
        TODO("NativeBitmap.asImage not implemented")
    }

    override fun clone(): Bitmap {
        TODO("NativeBitmap.clone not implemented")
    }
}