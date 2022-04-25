package baaahs.app.ui.settings

import baaahs.app.settings.UiSettings
import baaahs.app.ui.appContext
import baaahs.app.ui.dialog.dialogPanels
import baaahs.ui.withMouseEvent
import baaahs.ui.xComponent
import mui.material.*
import mui.system.Breakpoint
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

    Dialog {
        attrs.open = true
        attrs.onClose = { _, _ -> props.onClose() }
        attrs.maxWidth = Breakpoint.lg
        attrs.fullWidth = true

        DialogTitle { +"Settings" }

        DialogContent {
            dialogPanels {
                attrs.panels = panels
            }
        }

        DialogActions {
            ButtonGroup {
                Button {
                    attrs.disabled = true
                    attrs.onClick = props.onClose.withMouseEvent()
                    +"Revert"
                }

                Button {
                    attrs.onClick = props.onClose.withMouseEvent()
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