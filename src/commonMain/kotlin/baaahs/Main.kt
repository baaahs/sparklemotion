package baaahs

import kotlinx.coroutines.delay

lateinit var main: Main

//fun main(args: Array<String>) {
//    main = Main()
//    main.start()
//}

class Main {
    var display = getDisplay()
    var network = FakeNetwork(display = display.forNetwork())

    var sheepModel = SheepModel()
    val pinky = Pinky(network, display.forPinky())
    val mapper = Mapper(network, display.forMapper())

    fun start() {
        sheepModel.load()

        mapper.start()
        pinky.start()

        initThreeJs(sheepModel)
        sheepModel.panels.forEach { panel ->
            val jsPanelObj = addPanel(panel)
            SimBrain(network, display.forBrain(), JsPanel(jsPanelObj)).start()
        }
        startRender()

        doRunBlocking {
            delay(200000L)
        }
    }
}

class JsPanel(private val jsPanelObj: Any) {
    var color: Color = Color.BLACK
        set(value) {
            setPanelColor(jsPanelObj, value, (0..300).map { value }.toList())
            field = color
        }

    fun select() {
        selectPanel(jsPanelObj, true)
    }
}

external fun initThreeJs(sheepModel: SheepModel)
external fun addPanel(panel: SheepModel.Panel): Any
external fun startRender()
external fun selectPanel(panel: Any, isSelected: Boolean)
external fun setPanelColor(panel: Any, color: Color, pixelColors: List<Color>?)

expect fun getTimeMillis(): Long
expect fun doRunBlocking(block: suspend () -> Unit)

expect fun getResource(name: String): String