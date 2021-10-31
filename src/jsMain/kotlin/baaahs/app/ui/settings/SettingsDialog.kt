package baaahs.app.ui.settings

import baaahs.app.settings.UiSettings
import baaahs.app.ui.appContext
import baaahs.app.ui.dialog.dialogPanels
import baaahs.ui.withEvent
import baaahs.ui.xComponent
import kotlinx.html.js.onClickFunction
import materialui.components.button.button
import materialui.components.buttongroup.buttonGroup
import materialui.components.dialog.dialog
import materialui.components.dialog.enums.DialogMaxWidth
import materialui.components.dialogactions.dialogActions
import materialui.components.dialogcontent.dialogContent
import materialui.components.dialogtitle.dialogTitle
import react.Props
import react.RBuilder
import react.RHandler
import react.useContext

private val SettingsDialog = xComponent<SettingsDialogProps>("SettingsDialog") { props ->
    val appContext = useContext(appContext)
    val uiSettings = appContext.uiSettings
    val plugins = appContext.plugins

    val panels = listOf(
        MainSettingsPanel(props.changeUiSettings)
    )  + plugins.getSettingsPanels()

    dialog {
        attrs.open = true
        attrs.onClose = { _, _ -> props.onClose() }
        attrs.maxWidth = DialogMaxWidth.lg
        attrs.fullWidth = true

        dialogTitle { +"Settings" }

        dialogContent {
            dialogPanels {
                attrs.panels = panels
            }
        }

        dialogActions {
            buttonGroup {
                button {
                    attrs.disabled = true
                    attrs.onClickFunction = props.onClose.withEvent()
                    +"Revert"
                }

                button {
                    attrs.onClickFunction = props.onClose.withEvent()
                    +"Close"
                }
            }
        }
    }
}

external interface SettingsDialogProps : Props {
    var changeUiSettings: ((UiSettings) -> UiSettings) -> Unit
    var onClose: () -> Unit
}

fun RBuilder.settingsDialog(handler: RHandler<SettingsDialogProps>) =
    child(SettingsDialog, handler = handler)