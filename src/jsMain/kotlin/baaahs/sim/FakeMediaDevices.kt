package baaahs.sim

import baaahs.MediaDevices
import baaahs.browser.RealMediaDevices
import baaahs.imaging.Image
import baaahs.imaging.WebGlImage
import baaahs.visualizer.BaseVisualizer
import baaahs.visualizer.Visualizer
import js.objects.jso
import three.js.Camera
import three.js.PerspectiveCamera
import three.js.Scene
import three.js.WebGLRenderer
import web.gl.WebGLRenderingContext

class FakeMediaDevices(
    private val visualizer: Visualizer,
    private val realMediaDevices: RealMediaDevices?
) : MediaDevices {
    override suspend fun enumerate(): List<MediaDevices.Device> {
        return listOf(
            fakeDeviceSm, fakeDeviceMd, fakeDeviceLg
        ) + (realMediaDevices?.enumerate() ?: emptyList())
    }

    override fun getCamera(selectedDevice: MediaDevices.Device?): MediaDevices.Camera {
        return when (selectedDevice) {
            fakeDeviceSm -> {
                FakeCamera(320, 200).also { visualizer.addFrameListener(it) }
            }
            fakeDeviceMd, null -> {
                FakeCamera(640, 400).also { visualizer.addFrameListener(it) }
            }
            fakeDeviceLg -> {
                FakeCamera(1280, 800).also { visualizer.addFrameListener(it) }
            }
            else -> {
                realMediaDevices?.getCamera(selectedDevice)
                    ?: error("no RealMediaDevices?")
            }
        }
    }

    companion object {
        val fakeDeviceSm = MediaDevices.Device("simulatorSm", "videoinput", "Simulator 320x200", "group")
        val fakeDeviceMd = MediaDevices.Device("simulatorMd", "videoinput", "Simulator 640x400", "group")
        val fakeDeviceLg = MediaDevices.Device("simulatorLg", "videoinput", "Simulator 1280x800", "group")
    }

    inner class FakeCamera(val width: Int, val height: Int) : MediaDevices.Camera, BaseVisualizer.FrameListener {
        // offscreen renderer for virtual camera:
        private var camRenderer = WebGLRenderer(jso { preserveDrawingBuffer = true}).apply {
            setSize(width, height)
        }

        private val camCtx = camRenderer.getContext()
        private val altCamera = PerspectiveCamera(45, 1.0, 1, 10000)
        private val webGlImage = WebGlImage(camCtx.unsafeCast<WebGLRenderingContext>())

        override fun onFrameReady(scene: Scene, camera: Camera) {
            altCamera.copy(camera, true)
            altCamera.aspect = width.toDouble() / height
            altCamera.updateProjectionMatrix()
            camRenderer.render(scene, altCamera)

            onImage.invoke(webGlImage)
        }

        override var onImage: (image: Image) -> Unit = { _ -> }

        override fun close() {
            onImage = { _ -> }
            visualizer.removeFrameListener(this)
        }
    }
}
