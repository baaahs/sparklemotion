package baaahs.sim

import baaahs.MediaDevices
import baaahs.imaging.Image
import baaahs.imaging.ImageBitmapImage
import baaahs.visualizer.Visualizer
import info.laht.threekt.cameras.Camera
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.renderers.WebGLRenderer
import info.laht.threekt.scenes.Scene
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.Uint8ClampedArray
import org.w3c.dom.*
import kotlin.browser.window

class FakeMediaDevices(private val visualizer: Visualizer) : MediaDevices {
    var currentCam: MediaDevices.Camera? = null

    @JsName("getCurrentCam")
    fun getCurrentCam() = currentCam

    override fun getCamera(width: Int, height: Int): MediaDevices.Camera {
        return FakeCamera(width, height).also {
            visualizer.addFrameListener(it)
        }
    }

    inner class FakeCamera(val width: Int, val height: Int) : MediaDevices.Camera, Visualizer.FrameListener {
        // offscreen renderer for virtual camera:
        var camRenderer = WebGLRenderer(js("{preserveDrawingBuffer: true}")).apply {
            setSize(width, height)
        }

        private val camCtx = (camRenderer.domElement as HTMLCanvasElement).getContext("webgl")!!
        private val altCamera = PerspectiveCamera(45, 1.0, 1, 1000)
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
