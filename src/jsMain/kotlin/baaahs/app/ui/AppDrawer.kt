package baaahs.app.ui

import baaahs.io.Fs
import baaahs.ui.on
import baaahs.ui.unaryPlus
import baaahs.ui.withEvent
import baaahs.ui.xComponent
import kotlinx.css.Direction
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import materialui.components.divider.divider
import materialui.components.drawer.drawer
import materialui.components.drawer.enums.DrawerAnchor
import materialui.components.drawer.enums.DrawerStyle
import materialui.components.drawer.enums.DrawerVariant
import materialui.components.formcontrollabel.formControlLabel
import materialui.components.iconbutton.iconButton
import materialui.components.list.list
import materialui.components.listitem.listItem
import materialui.components.listitemicon.listItemIcon
import materialui.components.listitemtext.listItemText
import materialui.components.switches.switch
import materialui.components.typography.typographyH6
import materialui.icon
import materialui.styles.muitheme.direction
import materialui.useTheme
import org.w3c.dom.events.Event
import react.*
import react.dom.div

val AppDrawer = xComponent<AppDrawerProps>("AppDrawer", isPure = true) { props ->
    val appContext = useContext(appContext)
    val theme = useTheme()
    val themeStyles = ThemeStyles(theme)

    val handleDownloadShow by eventHandler { _: Event ->
        val show = appContext.webClient.show!!
        UiActions.downloadShow(show, appContext.plugins)
    }

    drawer(
        themeStyles.appDrawer on DrawerStyle.root,
        themeStyles.appDrawerPaper on DrawerStyle.paperAnchorLeft
    ) {
        attrs.variant = DrawerVariant.persistent
        attrs.anchor = DrawerAnchor.left
        attrs.open = props.open
        attrs.onClose = props.onClose.withEvent()

        div(+themeStyles.appDrawerHeader) {
            iconButton {
                attrs.onClickFunction = props.onClose.withEvent()
                if (theme.direction == Direction.ltr) {
                    icon(materialui.icons.ChevronLeft)
                } else {
                    icon(materialui.icons.ChevronRight)
                }
                if (props.forcedOpen) {
                    attrs.disabled = true
                }
            }
        }

        divider {}

        list {
            listItem {
                attrs.button = true
                attrs.onClickFunction = props.onNewShow.withEvent()
                listItemIcon { icon(materialui.icons.Add) }
                listItemText { attrs.primary { +"New Show…" } }
            }

            listItem {
                attrs.button = true
                attrs.onClickFunction = props.onOpenShow.withEvent()
                listItemIcon { icon(materialui.icons.OpenInBrowser) }
                listItemText { attrs.primary { +if (props.showLoaded) "Switch To…" else "Open…" } }
            }

            listItem {
                attrs.button = true
                attrs.disabled = !props.showUnsaved || props.showFile == null
                attrs.onClickFunction = props.onSaveShow.withEvent()
                listItemIcon { icon(materialui.icons.Save) }
                listItemText { attrs.primary { +"Save" } }
            }

            listItem {
                attrs.button = true
                attrs.onClickFunction = props.onSaveShowAs.withEvent()
                listItemIcon { icon(materialui.icons.FileCopy) }
                listItemText { attrs.primary { +"Save As…" } }
            }

            listItem {
                attrs.button = true
                attrs.disabled = appContext.webClient.show == null
                attrs.onClickFunction = handleDownloadShow
                listItemIcon { icon(CommonIcons.Download) }
                listItemText { attrs.primary { +"Download Show" } }
            }

            listItem {
                attrs.button = true
                attrs.disabled = appContext.webClient.show == null
                attrs.onClickFunction = props.onCloseShow.withEvent()
                listItemIcon { icon(materialui.icons.Close) }
                listItemText { attrs.primary { +"Close" } }
            }

            divider {}

            listItem {
                attrs.disabled = appContext.webClient.show == null
                formControlLabel {
                    attrs.control {
                        switch {
                            attrs.checked = props.editMode
                            attrs.onChangeFunction = props.onEditModeChange.withEvent()
                        }
                    }
                    attrs.label { typographyH6 { +"Design Mode" } }
                }
            }

            listItem {
                attrs.button = true
                attrs.disabled = appContext.webClient.show == null
                attrs.onClickFunction = props.onLayoutEditorDialogToggle.withEvent()
                listItemIcon { icon(CommonIcons.Layout) }
                listItemText { attrs.primary { +"Layout Editor" } }
            }
        }

        divider {}

        list {
            listItem {
                formControlLabel {
                    attrs.control {
                        switch {
                            attrs.checked = props.darkMode
                            attrs.onChangeFunction = props.onDarkModeChange.withEvent()
                        }
                    }
                    attrs.label { typographyH6 { +"Dark Mode" } }
                }
            }

            listItem {
                attrs.button = true
                attrs.onClickFunction = props.onSettings.withEvent()
                listItemIcon { icon(CommonIcons.Settings) }
                listItemText { attrs.primary { +"Settings" } }
            }
        }
    }
}

external interface AppDrawerProps : RProps {
    var open: Boolean
    var forcedOpen: Boolean
    var onClose: () -> Unit

    var showLoaded: Boolean
    var showFile: Fs.File?
    var editMode: Boolean
    var showUnsaved: Boolean
    var onEditModeChange: () -> Unit

    var onLayoutEditorDialogToggle: () -> Unit

    var darkMode: Boolean
    var onDarkModeChange: () -> Unit

    var onNewShow: () -> Unit
    var onOpenShow: () -> Unit
    var onSaveShow: () -> Unit
    var onSaveShowAs: () -> Unit
    var onCloseShow: () -> Unit

    var onSettings: () -> Unit
}

fun RBuilder.appDrawer(handler: RHandler<AppDrawerProps>) =
    child(AppDrawer, handler = handler)