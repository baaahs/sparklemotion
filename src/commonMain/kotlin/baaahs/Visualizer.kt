package baaahs

class Visualizer(private val sheepModel: SheepModel, private val dmxUniverse: FakeDmxUniverse) {
    fun start() {
        initThreeJs(sheepModel)
    }

    fun showPanel(panel: SheepModel.Panel): JsPanel {
        return JsPanel(addPanel(panel))
    }

    fun addEye(eye: SheepModel.MovingHead) {
        MovingHeadView(eye, dmxUniverse)
    }
}

class JsPanel(private val jsPanelObj: Any) {
    val pixelCount = 300

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

class JsPixels(private val jsPanel: JsPanel): Pixels {
    override val count = jsPanel.pixelCount

    override fun set(colors: Array<Color>) {
        jsPanel.setPixelsTo(colors)
    }
}

class MovingHeadView(private val movingHead: SheepModel.MovingHead, dmxUniverse: FakeDmxUniverse) {
    val baseChannel = Config.DMX_DEVICES[movingHead.name]!!
    val device = Shenzarpy(dmxUniverse.reader(baseChannel, 16) { receivedDmxFrame() })
    val movingHeadJs = addMovingHead(movingHead)

    private fun receivedDmxFrame() {
        val colorWheelV = device.colorWheel
        val wheelColor = Shenzarpy.WheelColor.get(colorWheelV)
        adjustMovingHead(movingHeadJs, wheelColor.color, device.pan, device.tilt)
    }
}

external fun initThreeJs(sheepModel: SheepModel)

external fun addPanel(panel: SheepModel.Panel): Any
external fun setPanelColor(panel: Any, color: Color, pixelColors: Array<Color>)

external fun addMovingHead(movingHead: SheepModel.MovingHead): Any
external fun adjustMovingHead(movingHeadJs: Any, color: Color, rotA: Float, rotB: Float)