package baaahs

import baaahs.imaging.Image
import baaahs.sim.FakeDmxUniverse
import baaahs.sim.FakeMediaDevices
import info.laht.threekt.cameras.Camera
import info.laht.threekt.scenes.Scene
import org.w3c.dom.HTMLElement

actual class Visualizer actual constructor(
    @JsName("sheepModel") private val sheepModel: SheepModel,
    private val dmxUniverse: FakeDmxUniverse) {
    actual val mediaDevices: MediaDevices = FakeMediaDevices({ width, height -> object: MediaDevices.Camera {
        override var onImage: (image: Image) -> Unit
            get() = TODO("FakeCamera.onImage not implemented")
            set(value) {}

        override fun close() {
            TODO("FakeCamera.close not implemented")
        }
    } })
    @JsName("frameListeners") val frameListeners = mutableListOf<FrameListener>()

    actual fun showPanel(panel: SheepModel.Panel): JsPanel {
        val maxPixelCount = 400
        return JsPanel(addPanel(panel), maxPixelCount)
    }

    actual fun addEye(eye: SheepModel.MovingHead) {
    }

    fun addFrameListener(frameListener: FrameListener) {
        frameListeners.add(frameListener)
    }

    fun removeFrameListener(frameListener: FrameListener) {
        frameListeners.remove(frameListener)
    }

    actual fun setMapperRunning(b: Boolean) {
        js("setMapperRunning")(b)
    }

    actual var onNewMapper: () -> Unit = {}
    actual var onNewUi: () -> Unit = {}
}

interface FrameListener {
    @JsName("onFrameReady")
    fun onFrameReady(scene: Scene, camera: Camera)
}

actual class JsPanel(private val vizPanel: dynamic, val pixelCount: Int) {
    fun setAllPixelsTo(color: Color) {
        setPanelColor(vizPanel, Color.WHITE, (0..pixelCount).map { color }.toTypedArray())
    }

    fun setPixelsTo(colors: Array<Color>) {
        vizPanel.setPanelColor(Color.WHITE, colors)
    }

    var color: Color = Color.BLACK
        set(value) {
            vizPanel.setColors(value, (0..pixelCount).map { value }.toTypedArray())
            field = color
        }
}

actual class JsPixels actual constructor(private val jsPanel: JsPanel) : Pixels {
    override val count = jsPanel.pixelCount

    override fun set(colors: Array<Color>) {
        jsPanel.setPixelsTo(colors)
    }
}

external fun initThreeJs(canvasContainer: HTMLElement, sheepModel: SheepModel, frameListeners: List<FrameListener>)

external fun addPanel(panel: SheepModel.Panel): Any
external fun setPanelColor(panel: Any, color: Color, pixelColors: Array<Color>)

external fun addMovingHead(movingHead: SheepModel.MovingHead): Any
external fun adjustMovingHead(movingHeadJs: Any, color: Color, dimmer: Float, pan: Float, tilt: Float)