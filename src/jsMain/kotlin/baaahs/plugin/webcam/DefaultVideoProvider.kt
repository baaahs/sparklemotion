package baaahs.plugin.webcam

import baaahs.document
import baaahs.util.Logger
import baaahs.window
import com.danielgergely.kgl.TextureResource
import kotlinx.js.jso
import org.w3c.dom.HTMLVideoElement

actual val DefaultVideoProvider: VideoProvider
    get() = BrowserWebCamVideoProvider

object BrowserWebCamVideoProvider : VideoProvider {
    private val videoElement = document.createElement("video").apply {
        setAttribute("style", "display:none")
        setAttribute("autoplay", "")
        setAttribute("playsinline", "")
        document.body!!.appendChild(this)
    } as HTMLVideoElement

    private val logger = Logger<BrowserWebCamVideoProvider>()

    init {
        logger.info { "Initializing." }
        window.navigator.mediaDevices.getUserMedia(jso {
            video = js(
                "({" +
                        "    width: { min: 320, ideal: 640, max: 1920 },\n" +
                        "    height: { min: 200, ideal: 480, max: 1080 }\n" +
                        "})"
            )
        }).then { stream ->
            logger.warn { "From getUserMedia" }
            console.warn("From getUserMedia: ", stream)
            // apply the stream to the video element used in the texture
            videoElement.srcObject = stream
            videoElement.play()
        }.catch { error ->
            logger.error { "From getUserMedia" }
            console.error("Unable to access the camera/webcam.", error)
        }
    }

    override fun getTextureResource(): TextureResource {
        return TextureResource(videoElement)
    }
}
