package baaahs.plugin.webcam

import baaahs.document
import baaahs.util.Logger
import com.danielgergely.kgl.TextureResource
import js.objects.jso
import js.promise.catch
import org.khronos.webgl.TexImageSource
import web.html.HTMLVideoElement
import web.media.streams.ConstrainULongRange
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
        onplay = { isPlaying = true }
    }.also {
        document.body.appendChild(it)
    }

    private val logger = Logger<BrowserWebCamVideoProvider>()

    private fun startCamera() {
        logger.info { "Initializing." }
        if (!window.isSecureContext) {
            // The browser considers our session to be not secure; see
            // https://developer.mozilla.org/en-US/docs/Web/Security/Secure_Contexts.
            console.warn("navigator.MediaDevices isn't available. Try connecting to localhost directly?")
            return
        }

        navigator.mediaDevices.getUserMedia(jso {
            video = jso {
                width = jso<ConstrainULongRange> { min = 320; ideal = 640; max = 1920 }
                height = jso<ConstrainULongRange> { min = 200; ideal = 480; max = 1080 }
            }
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

    override fun isReady(): Boolean = isPlaying
        .also { ensureOpen() }

    override fun getTextureResource(): TextureResource {
        ensureOpen()
        return TextureResource(videoElement.unsafeCast<TexImageSource>())
    }

    private fun ensureOpen() {
        if (!isOpen) {
            isOpen = true
            startCamera()
        }
    }

    private val Window.isSecureContext: Boolean
        get() = asDynamic().isSecureContext as Boolean
}
