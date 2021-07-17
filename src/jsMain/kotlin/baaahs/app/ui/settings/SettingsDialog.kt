package baaahs.app.ui.settings

import baaahs.app.ui.appContext
import baaahs.ui.withEvent
import baaahs.ui.xComponent
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import materialui.components.button.button
import materialui.components.buttongroup.buttonGroup
import materialui.components.dialog.dialog
import materialui.components.dialogactions.dialogActions
import materialui.components.dialogcontent.dialogContent
import materialui.components.dialogtitle.dialogTitle
import materialui.components.divider.divider
import materialui.components.formcontrollabel.formControlLabel
import materialui.components.list.list
import materialui.components.listitem.listItem
import materialui.components.switches.switch
import materialui.components.typography.typographyH6
import react.*

private val SettingsDialog = xComponent<SettingsDialogProps>("SettingsDialog") { props ->
    val appContext = useContext(appContext)
    val uiSettings = appContext.uiSettings

    val handleDarkModeChange by handler {
        props.changeUiSettings { it.copy(darkMode = !it.darkMode) }
    }
    val handleRenderButtonPreviewsChange by handler(uiSettings) {
        props.changeUiSettings { it.copy(renderButtonPreviews = !it.renderButtonPreviews) }
    }
    val handleUseSharedContextsChange by handler(uiSettings) {
        props.changeUiSettings { it.copy(useSharedContexts = !it.useSharedContexts) }
    }

    dialog {
        attrs.open = true
        attrs.onClose = { _, _ -> props.onClose() }

        dialogTitle { +"Settings" }
        dialogContent {

            list {
                listItem {
                    formControlLabel {
                        attrs.control {
                            switch {
                                attrs.checked = uiSettings.darkMode
                                attrs.onChangeFunction = handleDarkModeChange.withEvent()
                            }
                        }
                        attrs.label { typographyH6 { +"Dark Mode" } }
                    }
                }
            }

            divider {}

            list {
                listItem {
                    formControlLabel {
                        attrs.control {
                            switch {
                                attrs.checked = uiSettings.renderButtonPreviews
                                attrs.onChangeFunction = handleRenderButtonPreviewsChange.withEvent()
                            }
                        }
                        attrs.label { typographyH6 { +"Render Button Previews" } }
                    }
                }
            }

            divider {}

            list {
                listItem {
                    formControlLabel {
                        attrs.control {
                            switch {
                                attrs.checked = uiSettings.useSharedContexts
                                attrs.onChangeFunction = handleUseSharedContextsChange.withEvent()
                            }
                        }
                        attrs.label { typographyH6 { +"Use Shared Contexts" } }
                    }
                }
            }
        }

        dialogActions {
            buttonGroup {
                button {
                    attrs.onClickFunction = props.onClose.withEvent()
                    +"Close"
                }
            }
        }
    }
}

external interface SettingsDialogProps : RProps {
    var changeUiSettings: ((UiSettings) -> UiSettings) -> Unit
    var onClose: () -> Unit
}

fun RBuilder.settingsDialog(handler: RHandler<SettingsDialogProps>) =
    child(SettingsDialog, handler = handler)