package baaahs.app.ui.settings

import baaahs.app.settings.UiSettings
import baaahs.app.ui.appContext
import baaahs.app.ui.dialog.DialogPanel
import baaahs.ui.*
import mui.material.*
import react.*

class MainSettingsPanel(
    private var changeUiSettings: ((UiSettings) -> UiSettings) -> Unit
) : DialogPanel {
    override val title: String
        get() = "Main"

    override fun getView(): View = renderWrapper {
        mainSettingsPanel {
            attrs.changeUiSettings = changeUiSettings
        }
    }
}

private val MainSettingsPanelView = xComponent<MainSettingsPanelProps>("MainSettingsPanel") { props ->
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


    List {
        ListItem {
            FormControlLabel {
                attrs.control = buildElement {
                    Switch {
                        attrs.checked = uiSettings.darkMode
                        attrs.onChange = handleDarkModeChange.withTChangeEvent()
                    }
                }
                attrs.label = buildElement { typographyH6 { +"Dark Mode" } }
            }
        }
    }

    Divider {}

    List {
        ListItem {
            FormControlLabel {
                attrs.control = buildElement {
                    Switch {
                        attrs.checked = uiSettings.renderButtonPreviews
                        attrs.onChange = handleRenderButtonPreviewsChange.withTChangeEvent()
                    }
                }
                attrs.label = buildElement { typographyH6 { +"Render Button Previews" } }
            }
        }
    }

    Divider {}

    List {
        ListItem {
            FormControlLabel {
                attrs.control = buildElement {
                    Switch {
                        attrs.checked = uiSettings.useSharedContexts
                        attrs.onChange = handleUseSharedContextsChange.withTChangeEvent()
                    }
                }
                attrs.label = buildElement { typographyH6 { +"Use Shared Contexts" } }
            }
        }
    }
}

external interface MainSettingsPanelProps : Props {
    var changeUiSettings: ((UiSettings) -> UiSettings) -> Unit
}

fun RBuilder.mainSettingsPanel(handler: RHandler<MainSettingsPanelProps>) =
    child(MainSettingsPanelView, handler = handler)