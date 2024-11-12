package baaahs.ui.components

class NestedList<T>(
    items: List<T>,
    private val getChildren: (T) -> List<T>
) {
    private var byItem = mutableMapOf<T, NestedListItem<T>>()
    var listItems: List<NestedListItem<T>>
    val visibleItems get() = buildList {
        fun addVisibleItems(items: List<NestedListItem<*>>) {
            items.forEach {
                add(it)
                if (it.isOpen) addVisibleItems(it.children)
            }
        }
        addVisibleItems(listItems)
    }

    init {
        listItems = rebuild(items)
    }

    private fun rebuild(items: List<T>): List<NestedListItem<T>> {
        val oldByItem = byItem
        byItem = mutableMapOf()

        fun add(item: T): NestedListItem<T> {
            val listItem = oldByItem[item] ?: NestedListItem(item)
            listItem.children = getChildren(item).map { add(it) }
            byItem[listItem.item] = listItem
            return listItem
        }
        return items.map { add(it) }
    }

    fun update(items: List<T>) {
        listItems = rebuild(items)
    }

    fun select(item: T?) {
        /** @return true if the item (or a descendent) was found and selected. */
        fun NestedListItem<*>.apply(): Boolean {
            var anySelected = false
            if (this.item == item) {
                isSelected = true
                anySelected = true
            } else isSelected = false

            children.forEach {
                if (it.apply()) {
                    isOpen = true
                    anySelected = true
                }
            }

            return anySelected
        }
        listItems.forEach { it.apply() }
    }

    fun findParentListItem(item: T?): NestedListItem<T>? {
        fun NestedListItem<T>.search(myParent: NestedListItem<T>?): NestedListItem<T>? {
            if (this.item == item) return myParent

            for (child in children) {
                val result = child.search(this)
                if (result != null) return result
            }
            return null
        }

        for (topLevelItem in listItems) {
            val result = topLevelItem.search(null)
            if (result != null) return result
        }
        return null
    }
}