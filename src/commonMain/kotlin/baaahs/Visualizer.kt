package baaahs

import baaahs.sim.FakeDmxUniverse

expect class Visualizer(sheepModel: SheepModel, dmxUniverse: FakeDmxUniverse) {
    fun showPanel(panel: SheepModel.Panel): JsPanel
    fun addEye(eye: SheepModel.MovingHead)
    fun setMapperRunning(b: Boolean)

    val mediaDevices: MediaDevices
    var onNewMapper: () -> Unit
    var onNewUi: () -> Unit
}

expect class JsPixels(jsPanel: JsPanel) : Pixels

expect class JsPanel