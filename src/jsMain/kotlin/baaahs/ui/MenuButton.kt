package baaahs.ui

import kotlinext.js.jsObject
import kotlinx.html.js.onClickFunction
import materialui.components.clickawaylistener.clickAwayListener
import materialui.components.menu.menu
import materialui.components.menuitem.menuItem
import org.w3c.dom.Element
import org.w3c.dom.events.Event
import react.*
import react.dom.button
import react.dom.div

private val MenuButton = functionalComponent<MenuButtonProps> { props ->
    var anchorEl by useState<Element?> { null }

    val handleButtonClick = useCallback() { event: Event ->
        anchorEl = event.currentTarget as Element
    }

    val handleClickAway = useCallback { event: Event ->
        println("Clicked away!")
        anchorEl = null
    }

    clickAwayListener {
        attrs { onClickAway = handleClickAway }
        div {
            button { +props.name; attrs.onClickFunction = handleButtonClick }

            val items = props.items ?: emptyList()
            menu {
                attrs.getContentAnchorEl = null
                attrs.anchorEl(anchorEl)
                attrs.anchorOrigin = jsObject { horizontal = "left"; vertical = "bottom" }
                attrs.keepMounted = true
                attrs.open = anchorEl != null

                items.forEach { menuItem ->
                    val handleMenuClick = useCallback(menuItem.callback) { event: Event ->
                        anchorEl = null
                        menuItem.callback()
                    }

                    menuItem {
                        +menuItem.name
                        attrs.onClickFunction = handleMenuClick
                    }
                }
            }
        }
    }
}

class MenuItem(val name: String, val callback: () -> Unit)

fun RBuilder.menuButton(handler: MenuButtonProps.() -> Unit): ReactElement =
    child(MenuButton) { attrs { handler() } }

external interface MenuButtonProps : RProps {
    var name: String
    var items: List<MenuItem>?
}