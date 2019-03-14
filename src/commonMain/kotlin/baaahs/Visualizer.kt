package baaahs

class Visualizer(private val sheepModel: SheepModel) {
    fun start() {
        initThreeJs(sheepModel)
    }

    fun showPanel(panel: SheepModel.Panel): JsPanel {
        return JsPanel(addPanel(panel))
    }
}

class JsPanel(private val jsPanelObj: Any) {
    var color: Color = Color.BLACK
        set(value) {
            setPanelColor(jsPanelObj, value, (0..300).map { value }.toList())
            field = color
        }
}

external fun initThreeJs(sheepModel: SheepModel)
external fun addPanel(panel: SheepModel.Panel): Any
external fun startRender()
external fun setPanelColor(panel: Any, color: Color, pixelColors: List<Color>?)
