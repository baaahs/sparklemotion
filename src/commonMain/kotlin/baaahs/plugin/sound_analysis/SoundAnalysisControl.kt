package baaahs.plugin.sound_analysis

import baaahs.app.ui.dialog.DialogPanel
import baaahs.app.ui.editor.EditableManager
import baaahs.app.ui.editor.GenericPropertiesEditorPanel
import baaahs.app.ui.editor.PropsEditor
import baaahs.app.ui.editor.editorPanelViews
import baaahs.camelize
import baaahs.randomId
import baaahs.show.Control
import baaahs.show.live.ControlProps
import baaahs.show.live.OpenContext
import baaahs.show.live.OpenControl
import baaahs.show.mutable.MutableControl
import baaahs.show.mutable.MutableShow
import baaahs.show.mutable.ShowBuilder
import baaahs.ui.View
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
@SerialName("baaahs.SoundAnalysis:SoundAnalysis")
data class SoundAnalysisControl(
    val sonicRunwayMode: Boolean = false
) : Control {
    override val title: String get() = "SoundAnalysis"

    override fun createMutable(mutableShow: MutableShow): MutableControl {
        return MutableSoundAnalysisControl(sonicRunwayMode)
    }

    override fun open(id: String, openContext: OpenContext): OpenControl {
        return OpenSoundAnalysisControl(id, sonicRunwayMode)
    }
}

class MutableSoundAnalysisControl(
    var sonicRunwayMode: Boolean
) : MutableControl {
    override val title: String get() = "SoundAnalysis"

    override var asBuiltId: String? = null

    override fun getEditorPanels(editableManager: EditableManager<*>): List<DialogPanel> {
        return listOf(
            GenericPropertiesEditorPanel(editableManager, getPropertiesComponents())
        )
    }

    private fun getPropertiesComponents(): List<PropsEditor> {
        return listOf(SoundAnalysisPropsEditor(this))
    }

    override fun buildControl(showBuilder: ShowBuilder): SoundAnalysisControl {
        return SoundAnalysisControl(sonicRunwayMode)
    }

    override fun previewOpen(): OpenControl {
        return OpenSoundAnalysisControl(randomId(title.camelize()), sonicRunwayMode)
    }
}

class SoundAnalysisPropsEditor(
    private val mutableSoundAnalysisControl: MutableSoundAnalysisControl
) : PropsEditor {
    override fun getView(editableManager: EditableManager<*>): View =
        editorPanelViews.forSoundAnalysis(editableManager, mutableSoundAnalysisControl)
}

class OpenSoundAnalysisControl(
    override val id: String,
    val sonicRunwayMode: Boolean
) : OpenControl {
    override fun getState(): Map<String, JsonElement>? = null

    override fun applyState(state: Map<String, JsonElement>) {}

    override fun toNewMutable(mutableShow: MutableShow): MutableControl {
        return MutableSoundAnalysisControl(sonicRunwayMode)
    }

    override fun getView(controlProps: ControlProps): View =
        soundAnalysisViews.forControl(this, controlProps)
}

interface SoundAnalysisViews {
    fun forControl(openButtonControl: OpenSoundAnalysisControl, controlProps: ControlProps): View

    fun forSettingsPanel(): View
}

val soundAnalysisViews by lazy { getSoundAnalysisViews() }
expect fun getSoundAnalysisViews(): SoundAnalysisViews