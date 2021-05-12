package baaahs.browser

import baaahs.MediaDevices
import baaahs.document
import baaahs.imaging.Image
import baaahs.imaging.VideoElementImage
import baaahs.window
import kotlinext.js.jsObject
import kotlinx.coroutines.*
import org.w3c.dom.HTMLVideoElement
import org.w3c.dom.ImageBitmap
import org.w3c.dom.events.EventTarget
import org.w3c.dom.mediacapture.*
import kotlin.js.Promise

class RealMediaDevices : MediaDevices, CoroutineScope by MainScope() {
    override suspend fun enumerate(): List<MediaDevices.Device> {
        return window.navigator.mediaDevices.enumerateDevices().await()
            .filter { it.kind == MediaDeviceKind.VIDEOINPUT }
            .map { MediaDevices.Device(it.deviceId, it.kind.toString(), it.label, it.groupId) }
    }

    override fun getCamera(selectedDevice: MediaDevices.Device?): MediaDevices.Camera {
        return object : MediaDevices.Camera {
            val constraints = jsObject<dynamic> {
                if (selectedDevice != null) {
                    deviceId = selectedDevice.deviceId
                } else {
                    width = js("({ min: 1024, ideal: 1280, max: 1920 })")
                    height = js("({ min: 776, ideal: 720, max: 1080 })")
                }
            }

            val camPromise: Promise<MediaStream> =
                window.navigator.mediaDevices.getUserMedia(MediaStreamConstraints(video = constraints))
            lateinit var videoTrack: MediaStreamTrack
            //            lateinit var imageCapture: ImageCapture
            val videoEl = document.createElement("video") as HTMLVideoElement
            private var closed = false

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
                videoEl.pause()
            }

            suspend fun capture() {
                onImage(VideoElementImage(videoEl))

                if (!closed) {
                    try {
                        videoEl.asDynamic().requestVideoFrameCallback(this::capture)
                    } catch (e: Exception) {
                        delay(50)
                        capture()
                    }
                }
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
