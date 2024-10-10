package baaahs.control

import baaahs.app.ui.dialog.DialogPanel
import baaahs.app.ui.editor.EditableManager
import baaahs.app.ui.editor.GenericPropertiesEditorPanel
import baaahs.app.ui.editor.VisualizerPropsEditor
import baaahs.camelize
import baaahs.randomId
import baaahs.show.Control
import baaahs.show.live.ControlProps
import baaahs.show.live.OpenContext
import baaahs.show.live.OpenControl
import baaahs.show.live.controlViews
import baaahs.show.mutable.MutableControl
import baaahs.show.mutable.MutableShow
import baaahs.show.mutable.ShowBuilder
import baaahs.ui.View
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
@SerialName("baaahs.Core:Visualizer")
data class VisualizerControl(
    val surfaceDisplayMode: SurfaceDisplayMode = SurfaceDisplayMode.Continuous,
    val rotate: Boolean = false
): Control {
    enum class SurfaceDisplayMode {
        Pixels,
        Continuous
    }

    override val title: String get() = "Visualizer"

    override fun createMutable(mutableShow: MutableShow): MutableVisualizerControl {
        return MutableVisualizerControl(surfaceDisplayMode, rotate)
    }

    override fun open(id: String, openContext: OpenContext): OpenControl {
        return OpenVisualizerControl(id, this)
    }
}

data class MutableVisualizerControl(
    var surfaceDisplayMode: VisualizerControl.SurfaceDisplayMode = VisualizerControl.SurfaceDisplayMode.Continuous,
    var rotate: Boolean = false
) : MutableControl {
    override val title: String get() = "Visualizer"

    override var asBuiltId: String? = null

    override fun getEditorPanels(editableManager: EditableManager<*>): List<DialogPanel> = listOf(
        GenericPropertiesEditorPanel(
            editableManager,
            VisualizerPropsEditor(this)
        )
    )

    override fun buildControl(showBuilder: ShowBuilder): VisualizerControl {
        return VisualizerControl(surfaceDisplayMode, rotate)
    }

    override fun previewOpen(): OpenControl {
        return OpenVisualizerControl(randomId(title.camelize()), buildControl(ShowBuilder()))
    }
}

class OpenVisualizerControl(
    override val id: String,
    private val visualizerControl: VisualizerControl
) : OpenControl {
    override fun getState(): Map<String, JsonElement>? = null

    override fun applyState(state: Map<String, JsonElement>) {}

    override fun toNewMutable(mutableShow: MutableShow): MutableControl =
        visualizerControl.createMutable(mutableShow)

    override fun getView(controlProps: ControlProps): View =
        controlViews.forVisualizer(this, controlProps)

    val rotate get() = visualizerControl.rotate
}
