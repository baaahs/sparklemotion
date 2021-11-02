package baaahs.plugin.sound_analysis

import baaahs.app.ui.CommonIcons
import baaahs.app.ui.appContext
import baaahs.ui.asTextNode
import baaahs.ui.value
import baaahs.ui.xComponent
import baaahs.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.html.js.onChangeFunction
import materialui.components.divider.divider
import materialui.components.formcontrol.formControl
import materialui.components.formlabel.formLabel
import materialui.components.listitemicon.listItemIcon
import materialui.components.listitemtext.listItemText
import materialui.components.menuitem.menuItem
import materialui.components.select.select
import materialui.icon
import react.Props
import react.RBuilder
import react.RHandler
import react.useContext

private val SoundAnalysisSettingsPanelView =
    xComponent<SoundAnalysisSettingsPanelProps>("SoundAnalysisSettingsPanel") { _ ->
        val appContext = useContext(appContext)
        val plugin = appContext.plugins.findPlugin<SoundAnalysisPlugin>()
        val soundAnalyzer = plugin.soundAnalyzer
        var availableAudioInputs by state { soundAnalyzer.listAudioInputs() }
        var currentAudioInput by state { soundAnalyzer.currentAudioInput }

        soundAnalyzer.listen { newInputs: List<AudioInput>, newAudioInput: AudioInput? ->
            availableAudioInputs = newInputs
            currentAudioInput = newAudioInput
        }

        formControl {
            formLabel { +"Audio Input" }

            val none = "_NONE_"
            select {
                attrs.value(currentAudioInput?.id ?: none)
                attrs.renderValue<String> { (if (it == none) "None" else it).asTextNode() }

                attrs.onChangeFunction = {
                    val inputId = it.target.value
                    val input = availableAudioInputs.find { it.id == inputId }
                    alertErrors {
                        soundAnalyzer.switchTo(input)
                    }
                }

                menuItem {
                    attrs.value = none

                    listItemIcon { icon(CommonIcons.None) }
                    listItemText { +"None" }
                }

                divider {}

                availableAudioInputs.forEach { audioInput ->
                    menuItem {
                        attrs.value = audioInput.id

                        listItemIcon { icon(CommonIcons.SoundInput) }
                        listItemText { +audioInput.title }
                    }
                }
            }
        }
    }

fun alertErrors(block: suspend () -> Unit) {
    GlobalScope.launch {
        try {
            block()
        } catch(e: Exception) {
            window.alert(e.message ?: "Unknown error.")
            throw e
        }
    }
}

external interface SoundAnalysisSettingsPanelProps : Props {
}

fun RBuilder.soundAnalysisSettingsPanel(handler: RHandler<SoundAnalysisSettingsPanelProps>) =
    child(SoundAnalysisSettingsPanelView, handler = handler)
