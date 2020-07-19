package baaahs.app.ui

import baaahs.ui.on
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.css.Direction
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import materialui.*
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
import materialui.styles.muitheme.direction
import org.w3c.dom.events.Event
import react.RBuilder
import react.RHandler
import react.RProps
import react.child
import react.dom.div

val AppDrawer = xComponent<AppDrawerProps>("AppDrawer") { props ->
    val theme = useTheme()
    val themeStyles = ThemeStyles(theme)

    drawer(
        themeStyles.appDrawer on DrawerStyle.root,
        themeStyles.appDrawerPaper on DrawerStyle.paperAnchorLeft
    ) {
        attrs.variant = DrawerVariant.persistent
        attrs.anchor = DrawerAnchor.left
        attrs.open = props.open
        attrs.onClose = props.onClose

        div(+themeStyles.appDrawerHeader) {
            iconButton {
                attrs.onClickFunction = props.onClose
                if (theme.direction == Direction.ltr) {
                    icon(ChevronLeft)
                } else {
                    icon(ChevronRight)
                }
            }
        }

        divider {}

        list {
            listItem {
                formControlLabel {
                    attrs.control {
                        switch {
                            attrs.checked = props.editMode
                            attrs.onChangeFunction = props.onEditModeChange
                        }
                    }
                    attrs.label { typographyH6 { +"Design Mode" } }
                }

            }
            listItem {
                attrs.button = true
                attrs.disabled = !props.editMode
                attrs.onClickFunction = props.onShaderEditorDrawerToggle
                listItemIcon { icon(Code) }
                listItemText { attrs.primary { +"Shader Editor" } }
            }
            listItem {
                attrs.button = true
                attrs.disabled = !props.editMode
                attrs.onClickFunction = props.onLayoutEditorDialogToggle
                listItemIcon { icon(Dashboard) }
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
                            attrs.onChangeFunction = props.onDarkModeChange
                        }
                    }
                    attrs.label { typographyH6 { +"Dark Mode" } }
                }
            }
        }
    }
}

external interface AppDrawerProps : RProps {
    var open: Boolean
    var onClose: (Event) -> Unit
    var editMode: Boolean
    var onEditModeChange: (Event) -> Unit
    var onShaderEditorDrawerToggle: (Event) -> Unit
    var onLayoutEditorDialogToggle: (Event) -> Unit
    var darkMode: Boolean
    var onDarkModeChange: (Event) -> Unit
}

fun RBuilder.appDrawer(handler: RHandler<AppDrawerProps>) =
    child(AppDrawer, handler = handler)