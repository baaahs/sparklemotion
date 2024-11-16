package baaahs.app.ui

import baaahs.app.ui.document.documentMenu
import baaahs.client.document.DocumentManager
import baaahs.ui.*
import kotlinx.html.unsafe
import materialui.icon
import mui.icons.material.Article
import mui.material.*
import mui.material.styles.Theme
import mui.material.styles.useTheme
import mui.system.Breakpoint
import mui.system.Direction
import mui.system.useMediaQuery
import react.*
import react.dom.b
import react.dom.div
import react.dom.events.SyntheticEvent
import react.dom.i
import react.dom.span

private val AppDrawerView = xComponent<AppDrawerProps>("AppDrawer", isPure = true) { props ->
    val appContext = useContext(appContext)
    val theme = useTheme<Theme>()
    val isSmallScreen = useMediaQuery(theme.breakpoints.down(Breakpoint.sm))
    val themeStyles = appContext.allStyles.appUi
    val documentManager = observe(props.documentManager)
    val openDocument = documentManager.openDocument

    val handleAppModeChange by handler(props.onAppModeChange) { _: SyntheticEvent<*, *>, value: Any ->
        props.onAppModeChange(AppMode.valueOf(value.toString()))
    }

    val editMode = observe(documentManager.editMode)
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


    Drawer {
        attrs.className = -themeStyles.appDrawer
        attrs.classes = muiClasses {
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

                for (aMode in AppMode.entries) {
                    Tab {
                        attrs.className = -themeStyles.appModeTab
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

        if (isSmallScreen) {
            // Otherwise the document info is shown in the app toolbar.

            Box {
                attrs.className = -themeStyles.appDrawerDocInfo

                if (openDocument != null) {
                    documentManager.file?.let {
                        div(+themeStyles.titleFooter) {
                            icon(Article)
                            span { attrs.unsafe { +"&nbsp;" } }
                            +it.toString()
                        }
                    }

                    b { +openDocument.title }
                    if (documentManager.isUnsaved) i(+themeStyles.unsaved) { +"* (unsaved)" }
                }
            }
        }

        Divider {}

        List {
            attrs.dense = isSmallScreen

            documentMenu {
                attrs.documentManager = documentManager
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
            attrs.dense = isSmallScreen

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

            appContext.webClient.additionalDrawerItems.forEach { item ->
                ListItem { child(item) }
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

    var onSettings: () -> Unit
}

fun RBuilder.appDrawer(handler: RHandler<AppDrawerProps>) =
    child(AppDrawerView, handler = handler)