package baaahs

class Visualizer(private val sheepModel: SheepModel) {
    fun start() {
        initThreeJs(sheepModel)
    }

    fun showPanel(panel: SheepModel.Panel): JsPanel {
        return JsPanel(addPanel(panel))
    }

    fun addEye(eye: SheepModel.MovingHead, dmx: Dmx) {
        MovingHeadView(eye, dmx)
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

class MovingHeadView(movingHead: SheepModel.MovingHead, dmx: Dmx) {
    val dmxAddress = dmx.allocate(movingHead.name, 16)
    val movingHeadJs = addMovingHead(movingHead)

    fun setColor(color: Color) {
        setMovingHeadData(movingHeadJs, color, 0f, 0f)
    }
}

external fun initThreeJs(sheepModel: SheepModel)

external fun addPanel(panel: SheepModel.Panel): Any
external fun setPanelColor(panel: Any, color: Color, pixelColors: List<Color>?)

external fun addMovingHead(movingHead: SheepModel.MovingHead)
external fun setMovingHeadData(movingHeadJs: Any, color: Color, rotA: Float, rotB: Float)