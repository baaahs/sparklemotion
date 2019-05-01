package baaahs

import info.laht.threekt.cameras.Camera
import info.laht.threekt.scenes.Scene
import kotlin.browser.document

actual class Visualizer actual constructor(private val sheepModel: SheepModel, private val dmxUniverse: FakeDmxUniverse) {
    actual val mediaDevices: MediaDevices = FakeMediaDevices(this)
    private val frameListeners = mutableListOf<FrameListener>()

    actual fun start() {
        initThreeJs(sheepModel, frameListeners)
        document.getElementById("newMapperButton")!!.addEventListener("click", { onNewMapper() })
        document.getElementById("webUiButton")!!.addEventListener("click", { onNewUi() })
    }

    actual fun showPanel(panel: SheepModel.Panel): JsPanel {
        val maxPixelCount = 400
        return JsPanel(addPanel(panel), maxPixelCount)
    }

    actual fun addEye(eye: SheepModel.MovingHead) {
        MovingHeadView(eye, dmxUniverse)
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

actual class JsPanel(private val jsPanelObj: Any, val pixelCount: Int) {
    fun setAllPixelsTo(color: Color) {
        setPanelColor(jsPanelObj, Color.WHITE, (0..pixelCount).map { color }.toTypedArray())
    }

    fun setPixelsTo(colors: Array<Color>) {
        setPanelColor(jsPanelObj, Color.WHITE, colors)
    }

    var color: Color = Color.BLACK
        set(value) {
            setPanelColor(jsPanelObj, value, (0..pixelCount).map { value }.toTypedArray())
            field = color
        }
}

actual class JsPixels actual constructor(private val jsPanel: JsPanel) : Pixels {
    override val count = jsPanel.pixelCount

    override fun set(colors: Array<Color>) {
        jsPanel.setPixelsTo(colors)
    }
}

class MovingHeadView(movingHead: SheepModel.MovingHead, dmxUniverse: FakeDmxUniverse) {
    val baseChannel = Config.DMX_DEVICES[movingHead.name]!!
    val device = Shenzarpy(dmxUniverse.reader(baseChannel, 16) { receivedDmxFrame() })
    val movingHeadJs = addMovingHead(movingHead)

    private fun receivedDmxFrame() {
        val colorWheelV = device.colorWheel
        val wheelColor = Shenzarpy.WheelColor.get(colorWheelV)
        adjustMovingHead(movingHeadJs, wheelColor.color, device.dimmer, device.pan, device.tilt)
    }
}

external fun initThreeJs(sheepModel: SheepModel, frameListeners: List<FrameListener>)

external fun addPanel(panel: SheepModel.Panel): Any
external fun setPanelColor(panel: Any, color: Color, pixelColors: Array<Color>)

external fun addMovingHead(movingHead: SheepModel.MovingHead): Any
external fun adjustMovingHead(movingHeadJs: Any, color: Color, dimmer: Float, pan: Float, tilt: Float)