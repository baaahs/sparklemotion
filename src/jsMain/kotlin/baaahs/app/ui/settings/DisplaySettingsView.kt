package baaahs.app.ui.settings

import baaahs.app.settings.UiSettings
import baaahs.app.ui.CommonIcons
import baaahs.app.ui.appContext
import baaahs.document
import baaahs.ui.asTextNode
import baaahs.ui.withTChangeEvent
import baaahs.ui.xComponent
import js.objects.jso
import materialui.icon
import mui.base.Orientation
import mui.material.*
import mui.system.sx
import react.*
import web.cssom.UserSelect
import web.cssom.em
import web.dom.Element
import web.events.Event

private val DisplaySettingsView = xComponent<DisplaySettingsProps>("DisplaySettings") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.appUi

    var menuAnchor by state<Element?> { null }
    val showMenu by mouseEventHandler { event -> menuAnchor = event.target as Element? }
    val hideMenu = callback { _: Event?, _: String? -> menuAnchor = null }

    val handleDimmerChange by handler { _: Event, value: dynamic, _: Number ->
        val dimness = (1 - (value as Number).toFloat() / 100f).toString()
        document.body.style.setProperty("--dimmer-level", dimness)
    }

    val webClient = appContext.webClient
    observe(webClient)
    val uiSettings = webClient.uiSettings
    val handleUiSettingsChange by handler(webClient, uiSettings) { callback: (UiSettings) -> UiSettings ->
        val newUiSettings = callback(uiSettings)
        webClient.updateUiSettings(newUiSettings, saveToStorage = true)
    }
    val handleDarkModeChange = callback(handleUiSettingsChange) {
        handleUiSettingsChange { it.copy(darkMode = !it.darkMode) }
    }

    Tooltip {
        attrs.title = "Display Settings".asTextNode()

        IconButton {
            attrs.onClick = showMenu
            icon(CommonIcons.DisplaySettings)
        }
    }

    if (menuAnchor != null) {
        Menu {
            attrs.anchorEl = menuAnchor.asDynamic()
            attrs.anchorOrigin = jso {
                horizontal = "left"
                vertical = "bottom"
            }
            attrs.open = menuAnchor != null
            attrs.onClose = hideMenu

            ListItem {
                attrs.sx { paddingBottom = 0.em }
                Typography {
                    attrs.sx { userSelect = "none".unsafeCast<UserSelect>() }
                    +"Screen Brightness:"
                }
            }
            MenuItem {
                attrs.sx { paddingLeft = 1.em; paddingTop = 0.em }
                Slider {
                    attrs.defaultValue = 80
                    attrs.min = 5
                    attrs.max = 100
                    attrs.step = 1
                    attrs.orientation = Orientation.horizontal
                    attrs.onChange = handleDimmerChange
                }
            }

            Divider {}

            MenuItem {
                FormControlLabel {
                    attrs.control = buildElement {
                        Switch {
                            attrs.checked = uiSettings.darkMode
                            attrs.onChange = handleDarkModeChange.withTChangeEvent()
                        }
                    }
                    attrs.label = "Dark Mode".asTextNode()
                }

            }
        }
    }
}

external interface DisplaySettingsProps : Props {
}

fun RBuilder.displaySettings(handler: RHandler<DisplaySettingsProps>) =
    child(DisplaySettingsView, handler = handler)