package baaahs.browser

import baaahs.MediaDevices
import baaahs.document
import baaahs.imaging.Image
import baaahs.imaging.VideoElementImage
import baaahs.util.globalLaunch
import js.objects.jso
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.await
import kotlinx.coroutines.delay
import org.w3c.dom.ImageBitmap
import org.w3c.dom.events.EventTarget
import org.w3c.dom.mediacapture.*
import web.events.EventHandler
import web.html.HTMLVideoElement
import kotlin.js.Promise

class RealMediaDevices : MediaDevices, CoroutineScope by MainScope() {
    override suspend fun enumerate(): List<MediaDevices.Device> {
        return window.navigator.mediaDevices.enumerateDevices().await()
            .filter { it.kind == MediaDeviceKind.VIDEOINPUT }
            .map { MediaDevices.Device(it.deviceId, it.kind.toString(), it.label, it.groupId) }
            .also {
                it.forEach { device -> console.log("Camera:", device.label, device.deviceId) }
            }
    }

    override fun getCamera(selectedDevice: MediaDevices.Device?): MediaDevices.Camera {
        return object : MediaDevices.Camera {
            val constraints = MediaStreamConstraints(video = jso<dynamic> {
                if (selectedDevice != null) {
                    deviceId = selectedDevice.deviceId
                }
//                width = js("({ min: 640, ideal: 1280, max: 1920 })")
            })

            init {
                console.log("getCamera(${selectedDevice?.deviceId}) ->", constraints)
            }

            val camPromise: Promise<MediaStream> =
                window.navigator.mediaDevices.getUserMedia(constraints)
            lateinit var videoTrack: MediaStreamTrack
            lateinit var videoTrackSettings: MediaTrackSettings
            //            lateinit var imageCapture: ImageCapture
            val videoEl = document.createElement("video") as HTMLVideoElement
            private val videoElementImage = VideoElementImage(videoEl)
            private var closed = false

            init {
                videoEl.autoplay = true

                camPromise.then { stream: MediaStream ->
                    videoTrack = stream.getVideoTracks()[0]
                    videoTrackSettings = videoTrack.getSettings()
                    console.log("track:", videoTrack)
                    console.log("settings:", videoTrackSettings)
                    console.log("capabilities:", videoTrack.getCapabilities())

//                    imageCapture = ImageCapture(videoTrack)
                    videoEl.srcObject = stream
                    videoEl.controls = true

                    videoEl.oncanplay = EventHandler {
                        println("oncanplay")
                        globalLaunch { capture() }
                    }

                    videoEl.onended = EventHandler {
                        println("onended")
                    }

                    videoEl.onloadeddata = EventHandler {
                        println("onloadeddata")
                    }

                    globalLaunch {
                        videoEl.play()
                    }
                }.catch { t -> println("caught ${t}") }
            }


            override var onImage: (image: Image) -> Unit = {}

            override fun close() {
                closed = true
                videoEl.pause()
            }

            suspend fun capture() {
                onImage(videoElementImage)

                if (!closed) {
                    // Some browsers might not support requestVideoFrameCallback() yet.
                    try {
                        videoEl.asDynamic().requestVideoFrameCallback(this::globalLaunchCapture)
                    } catch (e: Exception) {
                        delay(50)
                        capture()
                    }
                }
            }

            suspend fun globalLaunchCapture() {
                globalLaunch { capture() }
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
