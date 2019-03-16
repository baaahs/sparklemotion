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
    fun setAllPixelsTo(color: Color) {
        setPanelColor(jsPanelObj, Color.WHITE, (0..300).map { color })
    }

    fun setPixelsTo(colors: MutableList<Color>) {
        setPanelColor(jsPanelObj, Color.WHITE, colors)
    }

    var color: Color = Color.BLACK
        set(value) {
            setPanelColor(jsPanelObj, value, (0..300).map { value }.toList())
            field = color
        }
}

class MovingHeadView(private val movingHead: SheepModel.MovingHead, dmxUniverse: FakeDmxUniverse) {
    val baseChannel = Config.DMX_DEVICES[movingHead.name]!!
    val device = Shenzarpy(dmxUniverse.reader(baseChannel, 16) { receivedDmxFrame() })
    val movingHeadJs = addMovingHead(movingHead)

    private fun receivedDmxFrame() {
        val colorWheelV = device.colorWheel
        val wheelColor = Shenzarpy.WheelColor.values()[colorWheelV.toInt()]
        adjustMovingHead(movingHeadJs, wheelColor.color, device.pan, device.tilt)
    }
}

external fun initThreeJs(sheepModel: SheepModel)

external fun addPanel(panel: SheepModel.Panel): Any
external fun setPanelColor(panel: Any, color: Color, pixelColors: List<Color>?)

external fun addMovingHead(movingHead: SheepModel.MovingHead): Any
external fun adjustMovingHead(movingHeadJs: Any, color: Color, rotA: Float, rotB: Float)