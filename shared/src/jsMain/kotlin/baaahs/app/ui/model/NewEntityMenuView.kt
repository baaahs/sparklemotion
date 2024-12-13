package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.scene.MutableScene
import baaahs.ui.withMouseEvent
import baaahs.ui.xComponent
import baaahs.util.CacheBuilder
import js.objects.jso
import mui.material.ListItemText
import mui.material.Menu
import mui.material.MenuItem
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.events.MouseEvent
import react.useContext
import web.dom.Element

private val NewEntityMenuView = xComponent<NewEntityMenuProps>("NewEntityMenu") { props ->
    val appContext = useContext(appContext)
    val entityTypes = appContext.plugins.entityTypes

    val handleSelect = memo(props.onSelect) {
        CacheBuilder<EntityType, (MouseEvent<*, *>) -> Unit> {
            { _: MouseEvent<*, *> -> props.onSelect(it) }
        }
    }

    Menu {
        attrs.anchorEl = props.menuAnchor.asDynamic()
        attrs.anchorOrigin = jso {
            horizontal = "left"
            vertical = "bottom"
        }
        attrs.open = props.menuAnchor != null
        attrs.onClose = props.onClose.withMouseEvent()

        entityTypes.forEach { entityType ->
            MenuItem {
                attrs.onClick = handleSelect[entityType]
                ListItemText { +entityType.addNewTitle }
            }
        }
    }
}

external interface NewEntityMenuProps : Props {
    var menuAnchor: Element?
    var onSelect: (entityType: EntityType) -> Unit
    var onClose: () -> Unit
}

fun RBuilder.newEntityMenu(handler: RHandler<NewEntityMenuProps>) =
    child(NewEntityMenuView, handler = handler)