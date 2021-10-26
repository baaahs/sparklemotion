package baaahs.plugin.sound_analysis

import baaahs.app.ui.dialog.DialogPanel
import baaahs.ui.Icon
import baaahs.ui.View

class SoundAnalysisSettingsPanel(
) : DialogPanel {
    override val title: String
        get() = "Sound Analysis"
    override val listSubhead: String
        get() = "Plugins"
    override val icon: Icon?
        get() = null

    override fun getView(): View = soundAnalysisViews.forSettingsPanel()
}