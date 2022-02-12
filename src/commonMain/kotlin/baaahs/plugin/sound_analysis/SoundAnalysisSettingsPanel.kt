package baaahs.plugin.sound_analysis

import baaahs.app.ui.CommonIcons
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
        get() = CommonIcons.SoundAnalysisControl

    override fun getView(): View = soundAnalysisViews.forSettingsPanel()
}