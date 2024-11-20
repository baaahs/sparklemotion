package baaahs.ui.components

import baaahs.ui.xComponent
import mui.icons.material.ExpandLess
import mui.icons.material.ExpandMore
import mui.material.*
import mui.system.sx
import react.PropsWithChildren
import react.RBuilder
import react.RHandler
import web.cssom.em
import web.cssom.px

private val NestedListItemView = xComponent<NestedListItemProps>("NestedListItem") { props ->
    observe(props.item)

    val handleFocus by focusEventHandler(props.onFocus, props.item) {
        props.onFocus?.invoke(props.item)
    }
    val handleClick by mouseEventHandler(props.onSelect, props.item) {
        props.onSelect?.invoke(props.item)
    }

    @Suppress("UNUSED_VARIABLE")
    val handleCollapseClick by mouseEventHandler(props.item) {
        props.item.isOpen = !props.item.isOpen
    }

    ListItemButton {
        attrs.key = props.getKey?.invoke(props.item.item!!)
        attrs.dense = true
        attrs.selected = props.item.isSelected
        attrs.disabled = props.disabled == true
        attrs.autoFocus = props.item.isSelected
        attrs.onFocus = handleFocus
        attrs.onClick = handleClick

        @Suppress("UNREACHABLE_CODE")
        ListItemText {
            attrs.sx {
                paddingLeft = props.nestLevel.em
            }
            with(props.renderer) {
                render(props.item.unsafeCast<Nothing>())
            }

            if (props.children != null) {
                IconButton {
                    attrs.size = Size.small
                    attrs.sx {
                        marginLeft = 1.em
                        padding = 0.px
                    }
                    attrs.onClick = handleCollapseClick
                    if (props.item.isOpen) ExpandLess {} else ExpandMore {}
                }
            }
        }
    }

    props.children?.let {
        Collapse {
            attrs.`in` = props.item.isOpen

            List {
                attrs.disablePadding = true
                child(it)
            }
        }
    }
}

external interface NestedListItemProps : PropsWithChildren {
    var item: NestedListItem<*>
    var getKey: ((Any) -> String?)?
    var renderer: Renderer<*>
    var disabled: Boolean?
    var nestLevel: Int
    var onFocus: ((NestedListItem<*>?) -> Unit)?
    var onSelect: ((NestedListItem<*>?) -> Unit)?
}

fun RBuilder.nestedListItem(handler: RHandler<NestedListItemProps>) =
    child(NestedListItemView, handler = handler)