package baaahs.app.ui.settings

import baaahs.app.settings.UiSettings
import baaahs.app.ui.appContext
import baaahs.app.ui.dialog.DialogPanel
import baaahs.ui.*
import kotlinx.html.js.onChangeFunction
import materialui.components.divider.divider
import materialui.components.formcontrollabel.formControlLabel
import materialui.components.list.list
import materialui.components.listitem.listItem
import materialui.components.switches.switch
import materialui.components.typography.typographyH6
import react.Props
import react.RBuilder
import react.RHandler
import react.useContext

class MainSettingsPanel(
    private var changeUiSettings: ((UiSettings) -> UiSettings) -> Unit
) : DialogPanel {
    override val title: String
        get() = "Main"
    override val listSubhead: String?
        get() = null
    override val icon: Icon?
        get() = null

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

external interface MainSettingsPanelProps : Props {
    var changeUiSettings: ((UiSettings) -> UiSettings) -> Unit
}

fun RBuilder.mainSettingsPanel(handler: RHandler<MainSettingsPanelProps>) =
    child(MainSettingsPanelView, handler = handler)