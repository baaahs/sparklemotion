package baaahs.plugin.webcam

import com.danielgergely.kgl.TextureResource
import com.github.sarxos.webcam.Webcam

actual val DefaultVideoProvider: VideoProvider
    get() = WebcamCaptureVideoProvider

object WebcamCaptureVideoProvider : VideoProvider {
    override fun getTextureResource(): TextureResource {
        val webcam = Webcam.getDefault()
        println("webcam = ${webcam}")
        error("doink!")
    }

}
