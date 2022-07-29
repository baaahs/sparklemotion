package baaahs.imaging

import baaahs.MediaDevices
import com.danielgergely.kgl.ByteBuffer
import java.awt.image.BufferedImage

class BufferedImageBitmap(
    private val image: BufferedImage
) : Bitmap {
    override val width: Int
        get() = image.width
    override val height: Int
        get() = image.height

    override fun drawImage(image: Image) {
        TODO("drawImage() not implemented")
    }

    override fun drawImage(
        image: Image,
        sX: Int, sY: Int, sWidth: Int, sHeight: Int,
        dX: Int, dY: Int, dWidth: Int, dHeight: Int
    ) {
        TODO("drawImage() not implemented")
    }

    override fun copyFrom(other: Bitmap) = TODO("copyFrom() not implemented")

    override fun lighten(other: Bitmap) = TODO("lighten() not implemented")

    override fun darken(other: Bitmap) = TODO("darken() not implemented")

    override fun subtract(other: Bitmap) = TODO("subtract() not implemented")

    override fun multiply(other: Bitmap) = TODO("not implemented")

    override fun withData(region: MediaDevices.Region, fn: (data: UByteClampedArray) -> Boolean) {
        TODO("withData() not implemented")
    }

    // TODO: This buffer copy is unnecessary; ByteBuffer should be able to wrap the raster's dataBuffer directly.
    override fun withGlBuffer(region: MediaDevices.Region, fn: (data: ByteBuffer) -> Unit) {
        val buf = ByteBuffer((width * height * 4) * 4)
        for (y in 0 until image.height) {
            val width = image.width
            for (x in 0 until width) {
                val color = image.getRGB(x, y)
                buf[(y * width + x) * 4 + 0] = (color ushr(16) and 0xFF).toByte()
                buf[(y * width + x) * 4 + 1] = (color ushr(8) and 0xFF).toByte()
                buf[(y * width + x) * 4 + 2] = (color ushr(0) and 0xFF).toByte()
                buf[(y * width + x) * 4 + 3] = (color ushr(24) and 0xFF ).toByte()
            }
        }
        fn(buf)
    }

    override fun asImage(): Image {
        TODO("asImage() not implemented")
    }

    override fun toDataUrl(): String {
        TODO("toDataUrl() not implemented")
    }

    override fun clone(): Bitmap {
        TODO("clone() not implemented")
    }
}