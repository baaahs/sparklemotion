package baaahs.plugin.webcam

import baaahs.document
import baaahs.util.Logger
import baaahs.util.globalLaunch
import com.danielgergely.kgl.TextureResource
import js.objects.jso
import org.khronos.webgl.TexImageSource
import web.events.EventHandler
import web.html.HTMLVideoElement
import web.media.streams.ConstrainULongRange
import web.media.streams.MediaStreamConstraints
import web.media.streams.MediaTrackConstraints
import web.navigator.navigator
import web.window.Window
import web.window.window

actual val DefaultVideoProvider: VideoProvider
    get() = BrowserWebCamVideoProvider

object BrowserWebCamVideoProvider : VideoProvider {
    private var isOpen = false
    private var isPlaying = false

    private val videoElement = (document.createElement("video") as HTMLVideoElement).apply {
        setAttribute("style", "display:none")
        autoplay = true
        playsInline = true
        onplay = EventHandler { isPlaying = true }
    }.also {
        document.body.appendChild(it)
    }

    private val logger = Logger<BrowserWebCamVideoProvider>()

    private suspend fun startCamera() {
        logger.info { "Initializing." }
        if (!window.isSecureContext) {
            // The browser considers our session to be not secure; see
            // https://developer.mozilla.org/en-US/docs/Web/Security/Secure_Contexts.
            console.warn("navigator.MediaDevices isn't available. Try connecting to localhost directly?")
            return
        }

        try {
            val mediaStream = navigator.mediaDevices.getUserMedia(MediaStreamConstraints(
                video = MediaTrackConstraints(
                    width = ConstrainULongRange(min = 320, ideal = 640, max = 1920),
                    height = ConstrainULongRange(min = 200, ideal = 480, max = 1080)
                )
            ))
            logger.warn { "From getUserMedia" }
            console.warn("From getUserMedia: ", mediaStream)
            // apply the stream to the video element used in the texture
            videoElement.srcObject = mediaStream
            videoElement.play()
        } catch(e: Exception) {
            logger.error(e) { "From getUserMedia" }
            console.error("Unable to access the camera/webcam.", e)
        }
    }

    override fun isReady(): Boolean = isPlaying
        .also { ensureOpen() }

    override fun getTextureResource(): TextureResource {
        ensureOpen()
        return TextureResource(videoElement.unsafeCast<TexImageSource>())
    }

    private fun ensureOpen() {
        if (!isOpen) {
            isOpen = true
            globalLaunch {
                startCamera()
            }
        }
    }

    private val Window.isSecureContext: Boolean
        get() = asDynamic().isSecureContext as Boolean
}
