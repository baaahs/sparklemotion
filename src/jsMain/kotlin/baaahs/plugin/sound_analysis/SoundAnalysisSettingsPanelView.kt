package baaahs.plugin.sound_analysis

import baaahs.app.ui.CommonIcons
import baaahs.app.ui.appContext
import baaahs.ui.asTextNode
import baaahs.ui.value
import baaahs.ui.xComponent
import baaahs.util.globalLaunch
import materialui.icon
import mui.material.*
import react.*
import react.dom.events.ChangeEvent

private val SoundAnalysisSettingsPanelView =
    xComponent<SoundAnalysisSettingsPanelProps>("SoundAnalysisSettingsPanel") { _ ->
        val appContext = useContext(appContext)
        val plugin = appContext.plugins.getPlugin<SoundAnalysisPlugin>()
        val soundAnalyzer = plugin.soundAnalyzer
        var availableAudioInputs by state { soundAnalyzer.listAudioInputs() }
        var currentAudioInput by state { soundAnalyzer.currentAudioInput }

        soundAnalyzer.listen { newInputs: List<AudioInput>, newAudioInput: AudioInput? ->
            availableAudioInputs = newInputs
            currentAudioInput = newAudioInput
        }

        FormControl {
            FormLabel { +"Audio Input" }

            val none = "_NONE_"
            Select<SelectProps<String>> {
                attrs.value = currentAudioInput?.id ?: none
                attrs.renderValue = { (if (it == none) "None" else it).asTextNode() }

                attrs.onChange = { event: ChangeEvent<*>, _: ReactNode ->
                    val inputId = event.target.value
                    val input = availableAudioInputs.find { it.id == inputId }
                    globalLaunch {
                        soundAnalyzer.switchTo(input)
                    }
                }

                MenuItem {
                    attrs.value = none

                    ListItemIcon { icon(CommonIcons.None) }
                    ListItemText { +"None" }
                }

                Divider {}

                availableAudioInputs.forEach { audioInput ->
                    MenuItem {
                        attrs.value = audioInput.id

                        ListItemIcon { icon(CommonIcons.SoundInput) }
                        ListItemText { +audioInput.title }
                    }
                }
            }
        }
    }

external interface SoundAnalysisSettingsPanelProps : Props {
}

fun RBuilder.soundAnalysisSettingsPanel(handler: RHandler<SoundAnalysisSettingsPanelProps>) =
    child(SoundAnalysisSettingsPanelView, handler = handler)
