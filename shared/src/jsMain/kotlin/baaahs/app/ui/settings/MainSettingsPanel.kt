package baaahs.app.ui.settings

import baaahs.app.settings.UiSettings
import baaahs.app.ui.appContext
import baaahs.app.ui.dialog.DialogPanel
import baaahs.ui.View
import baaahs.ui.renderWrapper
import baaahs.ui.typographyH6
import baaahs.ui.xComponent
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

    val handleDarkModeChange by switchEventHandler(uiSettings) { _, checked ->
        props.changeUiSettings { it.copy(darkMode = checked) }
    }
    val handleRenderButtonPreviewsChange by switchEventHandler(uiSettings) { _, checked ->
        props.changeUiSettings { it.copy(renderButtonPreviews = checked) }
    }
    val handleUseSharedContextsChange by switchEventHandler(uiSettings) { _, checked ->
        props.changeUiSettings { it.copy(useSharedContexts = checked) }
    }
    val handleDeveloperModeChange by switchEventHandler(uiSettings) { _, checked ->
        props.changeUiSettings { it.copy(developerMode = checked) }
    }


    List {
        ListItem {
            FormControlLabel {
                attrs.control = buildElement {
                    Switch {
                        attrs.checked = uiSettings.darkMode
                        attrs.onChange = handleDarkModeChange
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
                        attrs.onChange = handleRenderButtonPreviewsChange
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
                        attrs.checked = uiSettings.developerMode
                        attrs.onChange = handleDeveloperModeChange
                    }
                }
                attrs.label = buildElement { typographyH6 { +"Developer Mode" } }
            }
        }
    }

    if (uiSettings.developerMode) {
        Divider {}

        List {
            ListItem {
                FormControlLabel {
                    attrs.control = buildElement {
                        Switch {
                            attrs.checked = uiSettings.useSharedContexts
                            attrs.onChange = handleUseSharedContextsChange
                        }
                    }
                    attrs.label = buildElement { typographyH6 { +"Use Shared Contexts" } }
                }
            }
        }
    }
}

external interface MainSettingsPanelProps : Props {
    var changeUiSettings: ((UiSettings) -> UiSettings) -> Unit
}

fun RBuilder.mainSettingsPanel(handler: RHandler<MainSettingsPanelProps>) =
    child(MainSettingsPanelView, handler = handler)