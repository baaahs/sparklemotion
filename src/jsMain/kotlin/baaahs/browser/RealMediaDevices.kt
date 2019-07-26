package baaahs.browser

import baaahs.MediaDevices
import baaahs.imaging.Image
import baaahs.imaging.VideoElementImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLVideoElement
import org.w3c.dom.ImageBitmap
import org.w3c.dom.events.EventTarget
import org.w3c.dom.mediacapture.MediaStream
import org.w3c.dom.mediacapture.MediaStreamConstraints
import org.w3c.dom.mediacapture.MediaStreamTrack
import org.w3c.dom.mediacapture.MediaStreamTrackEvent
import kotlin.browser.document
import kotlin.browser.window
import kotlin.js.Promise

class RealMediaDevices : MediaDevices, CoroutineScope by MainScope() {
    override fun getCamera(): MediaDevices.Camera {
        return object : MediaDevices.Camera {
            val camPromise: Promise<MediaStream> =
                window.navigator.mediaDevices.getUserMedia(MediaStreamConstraints(video = js("({" +
                        "    width: { min: 1024, ideal: 1280, max: 1920 },\n" +
                        "    height: { min: 776, ideal: 720, max: 1080 }\n" +
                        "})")))
            lateinit var videoTrack: MediaStreamTrack
            //            lateinit var imageCapture: ImageCapture
            val videoEl = document.createElement("video") as HTMLVideoElement

            init {
                videoEl.autoplay = true

                camPromise.then { stream: MediaStream ->
                    videoTrack = stream.getVideoTracks()[0]
//                    imageCapture = ImageCapture(videoTrack)
                    videoEl.srcObject = stream
                    videoEl.controls = true
                    videoEl.play()

                    videoEl.oncanplay = {
                        println("oncanplay")
                        launch { capture() }
                    }

                    videoEl.onended = {
                        println("onended")
                    }

                    videoEl.onloadeddata = {
                        println("onloadeddata")
                    }

                }.catch { t -> println("caught ${t}") }
            }


            override var onImage: (image: Image) -> Unit = {}

            override fun close() {
            }

            suspend fun capture() {
                onImage(VideoElementImage(videoEl))

                delay(50)
                capture()
            }
        }
    }
}

public open external class ImageCapture() : EventTarget {
    constructor(track: MediaStreamTrack)

    //    constructor(tracks: Array<MediaStreamTrack>)
    open val id: String
    open val active: Boolean
    var onaddtrack: ((MediaStreamTrackEvent) -> dynamic)?
    var onremovetrack: ((MediaStreamTrackEvent) -> dynamic)?
    fun getAudioTracks(): Array<MediaStreamTrack>
    fun getVideoTracks(): Array<MediaStreamTrack>
    fun getTracks(): Array<MediaStreamTrack>
    fun getTrackById(trackId: String): MediaStreamTrack?
    fun addTrack(track: MediaStreamTrack): Unit
    fun removeTrack(track: MediaStreamTrack): Unit
    fun clone(): MediaStream
    fun grabFrame(): Promise<ImageBitmap>
}
