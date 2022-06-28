package baaahs.app.ui

import baaahs.app.ui.document.documentMenu
import baaahs.client.document.DocumentManager
import baaahs.ui.*
import kotlinx.js.jso
import materialui.icon
import mui.material.*
import mui.material.styles.Theme
import mui.material.styles.useTheme
import mui.system.Direction
import react.*
import react.dom.div
import react.dom.events.SyntheticEvent

val AppDrawer = xComponent<AppDrawerProps>("AppDrawer", isPure = true) { props ->
    val appContext = useContext(appContext)
    val theme = useTheme<Theme>()
    val themeStyles = appContext.allStyles.appUi

    val handleAppModeChange by handler(props.onAppModeChange) { _: SyntheticEvent<*, *>, value: String ->
        props.onAppModeChange(AppMode.valueOf(value))
    }

    val editMode = observe(props.documentManager.editMode)
    val handleEditModeChange by switchEventHandler(editMode) { _, _ ->
        editMode.toggle()
    }

    val handleClose by handler(props.onClose) { _: dynamic, _: String ->
        props.onClose()
    }

    val handleCloseButton by mouseEventHandler(props.onClose) {
        props.onClose()
    }

    val handleDarkModeChange by handler(props.onDarkModeChange) { _: SyntheticEvent<*, *>, _: Boolean ->
        props.onDarkModeChange()
    }

    val handleAutoModeChange by handler(props.onAutoModeChange) { _: SyntheticEvent<*, *>, _: Boolean ->
        props.onAutoModeChange()
    }


    Drawer {
        attrs.classes = jso {
            root = -themeStyles.appDrawer
            paperAnchorLeft = -themeStyles.appDrawerPaper
        }
        attrs.variant = DrawerVariant.persistent
        attrs.anchor = DrawerAnchor.left
        attrs.open = props.open
        attrs.onClose = handleClose

        div(+themeStyles.appDrawerHeader) {
            Tabs {
                attrs.value = props.appMode.name
                attrs.onChange = handleAppModeChange

                for (aMode in AppMode.values()) {
                    Tab {
                        attrs.classes = jso { root = -themeStyles.appModeTab }
                        attrs.value = aMode.name
                        attrs.label = ReactNode(aMode.name)
                    }
                }
            }

            IconButton {
                attrs.onClick = handleCloseButton
                if (theme.direction == Direction.ltr) {
                    icon(mui.icons.material.ChevronLeft)
                } else {
                    icon(mui.icons.material.ChevronRight)
                }
                if (props.forcedOpen == true) {
                    attrs.disabled = true
                }
            }
        }

        Divider {}

        List {
            documentMenu {
                attrs.documentManager = props.documentManager
            }

            Divider {}

            ListItem {
                attrs.disabled = !appContext.showManager.isLoaded
                FormControlLabel {
                    attrs.control = buildElement {
                        Switch {
                            attrs.checked = editMode.isOn
                            attrs.onChange = handleEditModeChange
                        }
                    }
                    attrs.label = "Design Mode".asTextNode()
                }
            }

            if (props.appMode == AppMode.Show) {
                ListItem {
                    ListItemButton {
                        attrs.onClick = props.onLayoutEditorDialogToggle.withMouseEvent()
                        ListItemIcon { icon(CommonIcons.Layout) }
                        ListItemText { +"Layout Editor" }
                    }
                    attrs.disabled = !appContext.showManager.isLoaded
                }
            }
        }

        Divider {}

        List {
            ListItem {
                FormControlLabel {
                    attrs.control = buildElement {
                        Switch {
                            attrs.checked = props.darkMode
                            attrs.onChange = handleDarkModeChange
                        }
                    }
                    attrs.onChange = handleDarkModeChange
                    attrs.label = "Dark Mode".asTextNode()
                }
            }

            ListItem {
                ListItemButton {
                    attrs.onClick = props.onSettings.withMouseEvent()
                    ListItemIcon { icon(CommonIcons.Settings) }
                    ListItemText { +"Settings" }
                }
            }

            FormControlLabel {
                attrs.control = buildElement {
                    Switch {
                        attrs.checked = props.autoMode
                        attrs.onChange = handleAutoModeChange
                    }
                }
                attrs.onChange = handleAutoModeChange
                attrs.label = "Auto Mode".asTextNode()
            }
        }
    }
}

external interface AppDrawerProps : Props {
    var open: Boolean?
    var forcedOpen: Boolean?
    var onClose: () -> Unit

    var appMode: AppMode
    var onAppModeChange: (AppMode) -> Unit

    var documentManager: DocumentManager<*, *>.Facade

    var onLayoutEditorDialogToggle: () -> Unit

    var darkMode: Boolean?
    var onDarkModeChange: () -> Unit

    var autoMode: Boolean?
    var onAutoModeChange: () -> Unit

    var onSettings: () -> Unit
}

fun RBuilder.appDrawer(handler: RHandler<AppDrawerProps>) =
    child(AppDrawer, handler = handler)