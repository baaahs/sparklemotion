package baaahs.control

import baaahs.ShowPlayer
import baaahs.app.ui.dialog.DialogPanel
import baaahs.app.ui.editor.EditableManager
import baaahs.camelize
import baaahs.gadgets.XyPad
import baaahs.geom.Vector2F
import baaahs.randomId
import baaahs.show.Control
import baaahs.show.DataSource
import baaahs.show.live.*
import baaahs.show.mutable.MutableControl
import baaahs.show.mutable.MutableShow
import baaahs.show.mutable.ShowBuilder
import baaahs.ui.View
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
@SerialName("baaahs.Core:XyPad")
data class XyPadControl(
    override val title: String,

    val initialValue: Vector2F = Vector2F.origin,

    val minValue: Vector2F = Vector2F.origin - Vector2F.unit2d,

    val maxValue: Vector2F = Vector2F.unit2d,

    override val controlledDataSourceId: String
) : Control {
    override fun createMutable(mutableShow: MutableShow): MutableXyPadControl {
        return MutableXyPadControl(
            title, initialValue, minValue, maxValue,
            mutableShow.findDataSource(controlledDataSourceId).dataSource
        )
    }

    override fun open(id: String, openContext: OpenContext, showPlayer: ShowPlayer): OpenXyPadControl {
        val controlledDataSource = openContext.getDataSource(controlledDataSourceId)
        val xyPad = XyPad(title, initialValue, minValue, maxValue)
        return OpenXyPadControl(id, xyPad, controlledDataSource)
            .also { showPlayer.registerGadget(id, xyPad, controlledDataSource) }
    }
}

class MutableXyPadControl(
    override var title: String,
    var initialValue: Vector2F = Vector2F.origin,
    var minValue: Vector2F = Vector2F.origin - Vector2F.unit2d,
    var maxValue: Vector2F = Vector2F.unit2d,
    var controlledDataSource: DataSource
) : MutableControl {
    override var asBuiltId: String? = null

    override fun getEditorPanels(editableManager: EditableManager<*>): List<DialogPanel> = emptyList()

    override fun buildControl(showBuilder: ShowBuilder): XyPadControl {
        return XyPadControl(
            title, initialValue, minValue, maxValue,
            showBuilder.idFor(controlledDataSource)
        )
    }

    override fun previewOpen(): OpenControl {
        val xyPad = XyPad(title, initialValue, minValue, maxValue)
        return OpenXyPadControl(randomId(title.camelize()), xyPad, controlledDataSource)
    }
}

class OpenXyPadControl(
    override val id: String,
    val xyPad: XyPad,
    override val controlledDataSource: DataSource
) : DataSourceOpenControl() {
    override val gadget: XyPad
        get() = xyPad

    override fun getState(): Map<String, JsonElement> = xyPad.state

    override fun applyState(state: Map<String, JsonElement>) = xyPad.applyState(state)

    override fun toNewMutable(mutableShow: MutableShow): MutableControl =
        MutableXyPadControl(xyPad.title, xyPad.initialValue, xyPad.minValue, xyPad.maxValue, controlledDataSource)

    override fun controlledDataSources(): Set<DataSource> =
        setOf(controlledDataSource)

    override fun getView(controlProps: ControlProps): View =
        controlViews.forXyPad(this, controlProps)
}