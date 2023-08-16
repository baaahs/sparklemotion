package baaahs.plugin.webcam

import baaahs.Color
import com.danielgergely.kgl.ByteBuffer
import com.danielgergely.kgl.TextureResource
import com.github.eduramiba.webcamcapture.drivers.NativeDriver
import com.github.sarxos.webcam.Webcam
import org.lwjgl.opengl.GL11

actual val DefaultVideoProvider: VideoProvider
    get() = WebcamCaptureVideoProvider


object WebcamCaptureVideoProvider : VideoProvider {
    private val webcam: Webcam = run {
        Webcam.setDriver(NativeDriver())
        Webcam.getDefault()
    }

    init {
        webcam.open()
    }

    override fun getTextureResource(): TextureResource {
        val image = webcam.image
        val vals = ByteBuffer(image.width * image.height * 3)
        var maxPix = 0
        for (y in 0 until image.height) {
            for (x in 0 until image.width) {
                val pixel = image.getRGB(x, y)
                maxPix = Integer.max(maxPix and 0xFFFFFF, pixel and 0xFFFFFF)
                val color = Color(pixel)
                val bufOffset = (y * image.width + x) * 3
                vals[bufOffset + 0] = color.redB
                vals[bufOffset + 1] = color.greenB
                vals[bufOffset + 2] = color.blueB
            }
        }

        println("maxPix $maxPix")

        return TextureResource(
            width = image.width,
            height = image.height,
            format = GL11.GL_RGB,
            type = GL11.GL_UNSIGNED_BYTE,
            data = vals
        )
    }

}
