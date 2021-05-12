package baaahs.sim

import baaahs.MediaDevices
import baaahs.browser.RealMediaDevices
import baaahs.imaging.Image
import baaahs.imaging.ImageBitmapImage
import baaahs.visualizer.Visualizer
import baaahs.window
import kotlinext.js.jsObject
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.Uint8ClampedArray
import org.w3c.dom.FLIPY
import org.w3c.dom.ImageBitmapOptions
import org.w3c.dom.ImageData
import org.w3c.dom.ImageOrientation
import three.js.Camera
import three.js.PerspectiveCamera
import three.js.Scene
import three.js.WebGLRenderer

class FakeMediaDevices(
    private val visualizer: Visualizer,
    private val realMediaDevices: RealMediaDevices?
) : MediaDevices {
    var currentCam: MediaDevices.Camera? = null

    override suspend fun enumerate(): List<MediaDevices.Device> {
        return listOf(
            fakeDevice
        ) + (realMediaDevices?.enumerate() ?: emptyList())
    }

    @JsName("getCurrentCam")
    fun getCurrentCam() = currentCam

    override fun getCamera(selectedDevice: MediaDevices.Device?): MediaDevices.Camera {
        return if (selectedDevice == fakeDevice) {
            FakeCamera(640, 480).also {
                visualizer.addFrameListener(it)
            }
        } else {
            realMediaDevices?.getCamera(selectedDevice)
                ?: error("no RealMediaDevices?")
        }
    }

    companion object {
        val fakeDevice = MediaDevices.Device("fake", "video", "Fake Camera", "group")
    }

    inner class FakeCamera(val width: Int, val height: Int) : MediaDevices.Camera, Visualizer.FrameListener {
        // offscreen renderer for virtual camera:
        var camRenderer = WebGLRenderer(jsObject { preserveDrawingBuffer = true}).apply {
            setSize(width, height)
        }

        private val camCtx = camRenderer.getContext()
        private val altCamera = PerspectiveCamera(45, 1.0, 1, 10000)
        private val pixelBuffer = Uint8ClampedArray(width * height * 4)
        private val imageData = ImageData(pixelBuffer, width, height)

        override fun onFrameReady(scene: Scene, camera: Camera) {
            altCamera.copy(camera, true)
            altCamera.aspect = width.toDouble() / height
            altCamera.updateProjectionMatrix()
            camRenderer.render(scene, altCamera)

            camCtx.asDynamic().readPixels(
                0, 0, width, height, camCtx.asDynamic().RGBA, camCtx.asDynamic().UNSIGNED_BYTE,
                Uint8Array(pixelBuffer.buffer)
            )

            window.createImageBitmap(imageData, ImageBitmapOptions().apply {
                imageOrientation = ImageOrientation.Companion.FLIPY
            }).then { onImage.invoke(ImageBitmapImage(it)) }
        }

        override var onImage: (image: Image) -> Unit = { _ -> }

        override fun close() {
            onImage = { _ -> }
            visualizer.removeFrameListener(this)
        }
    }
}
