package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.ui.withMouseEvent
import baaahs.ui.xComponent
import baaahs.util.CacheBuilder
import js.objects.jso
import mui.material.ListItemText
import mui.material.Menu
import mui.material.MenuItem
import mui.material.styles.Theme
import mui.material.styles.useTheme
import mui.system.sx
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.events.MouseEvent
import react.useContext
import web.cssom.Color
import web.cssom.em
import web.dom.Element

private val NewEntityMenuView = xComponent<NewEntityMenuProps>("NewEntityMenu") { props ->
    val appContext = useContext(appContext)
    val theme = useTheme<Theme>()
    val entityTypes = appContext.plugins.entityTypes

    val handleSelect = memo(props.onSelect) {
        CacheBuilder<EntityType, (MouseEvent<*, *>) -> Unit> {
            { _: MouseEvent<*, *> -> props.onSelect(it) }
        }
    }

    Menu {
        if (props.header != null) {
            attrs.sx { paddingTop = 0.em }
        }
        attrs.anchorEl = props.menuAnchor.asDynamic()
        attrs.anchorOrigin = jso {
            horizontal = "left"
            vertical = "bottom"
        }
        attrs.open = props.menuAnchor != null
        attrs.onClose = props.onClose.withMouseEvent()

        props.header?.let { header ->
            MenuItem {
                attrs.sx {
                    backgroundColor = Color(theme.palette.background.paper)
                    paddingLeft = 1.em
                    paddingRight = 1.em
                }
                attrs.dense = true
                attrs.disableGutters = true
                attrs.disabled = true
                ListItemText { +header }
            }
        }

        entityTypes.forEach { entityType ->
            MenuItem {
                attrs.dense = true
                attrs.onClick = handleSelect[entityType]
                ListItemText { +entityType.addNewTitle }
            }
        }
    }
}

external interface NewEntityMenuProps : Props {
    var menuAnchor: Element?
    var header: String?
    var onSelect: (entityType: EntityType) -> Unit
    var onClose: () -> Unit
}

fun RBuilder.newEntityMenu(handler: RHandler<NewEntityMenuProps>) =
    child(NewEntityMenuView, handler = handler)