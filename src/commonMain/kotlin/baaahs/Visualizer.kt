package baaahs

expect class Visualizer(sheepModel: SheepModel, dmxUniverse: FakeDmxUniverse) {
    fun start()
    fun showPanel(panel: SheepModel.Panel): JsPanel
    fun addEye(eye: SheepModel.MovingHead)
    fun setMapperRunning(b: Boolean)

    val mediaDevices: MediaDevices
    var onStartMapper: () -> Unit
}

expect class JsPixels(jsPanel: JsPanel) : Pixels

expect class JsPanel