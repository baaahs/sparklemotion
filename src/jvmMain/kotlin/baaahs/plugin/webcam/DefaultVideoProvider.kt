package baaahs.plugin.webcam

import com.github.eduramiba.webcamcapture.drivers.NativeDriver;
import com.danielgergely.kgl.IntBuffer
import com.danielgergely.kgl.TextureResource
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
        val vals = IntBuffer(image.width * image.height)
        for (x in 0..image.width) {
            for (y in 0..image.height) {
                vals[x*image.width + y] = image.getRGB(x, y)
            }
        }

        return TextureResource(
            width = image.width,
            height = image.height,
            format = GL11.GL_RGB,
            type = GL11.GL_UNSIGNED_BYTE,
            data = vals
        )
    }

}
