package baaahs.plugin.sound_analysis

import baaahs.app.settings.UiSettings
import baaahs.app.ui.appContext
import baaahs.ui.value
import baaahs.ui.xComponent
import baaahs.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.html.js.onChangeFunction
import materialui.components.divider.divider
import materialui.components.listitemtext.listItemText
import materialui.components.menuitem.menuItem
import materialui.components.select.select
import react.Props
import react.RBuilder
import react.RHandler
import react.useContext

private val SoundAnalysisSettingsPanelView =
    xComponent<SoundAnalysisSettingsPanelProps>("SoundAnalysisSettingsPanel") { props ->
        val appContext = useContext(appContext)
        val plugin = appContext.plugins.findPlugin<SoundAnalysisPlugin>()
        val soundAnalyzer = plugin.soundAnalyzer
        val currentAudioInput = soundAnalyzer.currentAudioInput
        val availableAudioInputs = soundAnalyzer.listAudioInputs()

        val none = "_NONE_"
        select {
            attrs.value(currentAudioInput?.id ?: none)

            attrs.onChangeFunction = {
                val inputId = it.target.value
                val input = availableAudioInputs.find { it.id == inputId }
                alertErrors {
                    soundAnalyzer.switchTo(input)
                }
            }

            menuItem {
                attrs.value = none

                listItemText { +"None" }
            }

            divider {}

            availableAudioInputs.forEach { audioInput ->
                menuItem {
                    attrs.value = audioInput.id

                    listItemText { +audioInput.title }
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
    var changeUiSettings: ((UiSettings) -> UiSettings) -> Unit
}

fun RBuilder.soundAnalysisSettingsPanel(handler: RHandler<SoundAnalysisSettingsPanelProps>) =
    child(SoundAnalysisSettingsPanelView, handler = handler)
