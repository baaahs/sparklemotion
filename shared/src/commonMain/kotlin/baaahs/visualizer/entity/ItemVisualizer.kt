package baaahs.visualizer.entity

import baaahs.device.MovingHeadDevice
import baaahs.device.PixelArrayDevice
import baaahs.dmx.MutableDmxTransportConfig
import baaahs.fixtures.FixtureConfig
import baaahs.geom.Vector3F
import baaahs.io.ByteArrayReader
import baaahs.scene.*
import baaahs.ui.IObservable
import baaahs.ui.View
import baaahs.visualizer.VizObj

interface ItemVisualizer<T: Any> : IObservable {
    val item: T
    val title: String
    var isEditing: Boolean
    var mapperIsRunning: Boolean
    var selected: Boolean
    val obj: VizObj
    val normal: Vector3F? get() = null

    fun notifyChanged()

    /** Returns `newItem` if this visualizer is capable of visualizing this item. */
    fun isApplicable(newItem: Any): T?

    fun update(newItem: T)

    /** Returns `true` if the three model has been updated to reflect `newEntity`. */
    fun updateIfApplicable(newEntity: T): Boolean {
        if (newEntity == item) return true

        val tEntity = isApplicable(newEntity)
        return if (tEntity != null) {
            update(tEntity)
            true
        } else false
    }

    fun find(predicate: (Any) -> Boolean): ItemVisualizer<*>? =
        if (predicate(item)) this else null

    fun traverse(callback: (ItemVisualizer<*>) -> Unit) {
        callback.invoke(this)
    }

    fun applyStyles()

    fun receiveFixtureConfig(fixtureConfig: FixtureConfig) {
        // No-op by default.
    }

    fun receiveRemoteFrameData(reader: ByteArrayReader) {
        // No-op by default.
    }
}

interface VisualizerBuilder {
    fun getTitleAndDescEditorView(editingEntity: EditingEntity<out MutableEntity>): View
    fun getTransformEditorView(editingEntity: EditingEntity<out MutableEntity>): View
    fun getGridEditorView(editingEntity: EditingEntity<out MutableGridData>): View
    fun getLightBarEditorView(editingEntity: EditingEntity<out MutableLightBarData>): View
    fun getLightRingEditorView(editingEntity: EditingEntity<out MutableLightRingData>): View
    fun getMovingHeadEditorView(editingEntity: EditingEntity<out MutableMovingHeadData>): View
    fun getImportedEntityEditorView(editingEntity: EditingEntity<out MutableImportedEntityGroup>): View

    // Controllers:
    fun getBrainControllerEditorView(editingController: EditingController<MutableBrainControllerConfig>): View
    fun getDirectDmxControllerEditorView(editingController: EditingController<MutableDirectDmxControllerConfig>): View
    fun getSacnControllerEditorView(editingController: EditingController<MutableSacnControllerConfig>): View

    // FixtureOptions:
    fun getMovingHeadFixtureOptionsEditorView(
        editingController: EditingController<*>,
        mutableFixtureOptions: MovingHeadDevice.MutableOptions
    ): View
    fun getPixelArrayFixtureOptionsEditorView(
        editingController: EditingController<*>,
        mutableFixtureOptions: PixelArrayDevice.MutableOptions
    ): View

    // TransportConfigs:
    fun getDmxTransportConfigEditorView(
        editingController: EditingController<*>,
        mutableTransportConfig: MutableDmxTransportConfig
    ): View
}

expect val visualizerBuilder: VisualizerBuilder