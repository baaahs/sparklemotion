package baaahs.ui.components

import baaahs.ui.xComponent
import mui.material.List
import react.Props
import react.RBuilder
import react.RHandler
import web.keyboard.KeyCode

private val NestedListView = xComponent<NestedListProps<*>>("NestedList") { props ->
    val items = props.nestedList.listItems

    var focusedItem by state<NestedListItem<*>?> { null }
    var selectedItem by state<NestedListItem<*>?> { null }

    val handleListItemFocus by handler(props.onFocus) { item: NestedListItem<*>? ->
        focusedItem?.isFocused = false
        focusedItem = item
        props.onFocus?.invoke(item?.item.unsafeCast<Nothing?>())
        Unit
    }

    val handleListItemSelect by handler(props.onSelect) { item: NestedListItem<*>? ->
        selectedItem?.isSelected = false
        selectedItem = item
        props.onSelect?.invoke(item?.item.unsafeCast<Nothing?>())
        Unit
    }

    val handleKeyDown by keyboardEventHandler(props.nestedList, handleListItemSelect) { e ->
        when (e.code) {
            KeyCode.ArrowUp -> {
                val visibleItems = props.nestedList.visibleItems
                val i = visibleItems.indexOf(selectedItem)
                if (i > 0) {
                    handleListItemSelect(visibleItems[i - 1])
                    e.preventDefault()
                }
            }
            KeyCode.ArrowDown -> {
                val visibleItems = props.nestedList.visibleItems
                val i = visibleItems.indexOf(selectedItem)
                if (i < visibleItems.size - 1) {
                    handleListItemSelect(visibleItems[i + 1])
                    e.preventDefault()
                }
            }
            KeyCode.ArrowLeft -> {
                if (selectedItem?.isOpen != true) {
                    val item = selectedItem?.item
                    props.nestedList.findParentListItem(item.unsafeCast<Nothing?>())?.let {
                        handleListItemSelect(it)
                    }
                } else if (selectedItem?.children?.isNotEmpty() == true)
                    selectedItem?.isOpen = false
            }
            KeyCode.ArrowRight -> {
                if (selectedItem?.children?.isNotEmpty() == true)
                    selectedItem?.isOpen = true
            }

//            KeyCode.Enter -> visualizer.selectedEntity?.let {
//                handleListItemSelect(mutableModel.findById(it.id))
//            }
            else -> {}
        }
    }

    fun RBuilder.renderItems(items: List<NestedListItem<*>>, nestLevel: Int = 0) {
        items.forEach { item ->
            val searchMatcher = props.searchMatcher
            val matchesSearch = searchMatcher == null
                    || searchMatcher(item.item.unsafeCast<Nothing>())

            nestedListItem {
                attrs.item = item
                attrs.getKey = props.getKey.unsafeCast<(Any) -> String?>()
                attrs.renderer = props.renderer
                attrs.disabled = !matchesSearch
                attrs.nestLevel = nestLevel
                attrs.onFocus = handleListItemFocus
                attrs.onSelect = handleListItemSelect

                if (item.children.isNotEmpty()) {
                    renderItems(item.children, nestLevel + 1)
                }
            }
        }
    }

    List {
        attrs.dense = true
        attrs.onKeyDown = handleKeyDown

        renderItems(items)
    }
}

external interface NestedListProps<T> : Props {
    var nestedList: NestedList<T>
    var getKey: ((T) -> String?)?
    var renderer: Renderer<T>
    var onFocus: ((T?) -> Unit)?
    var onSelect: ((T?) -> Unit)?
    var searchMatcher: ((T) -> Boolean)?
}

fun interface Renderer<T> {
    fun RBuilder.render(item: NestedListItem<T>)
}

fun <T> RBuilder.nestedList(handler: RHandler<NestedListProps<T>>) =
    child(NestedListView, handler = handler)