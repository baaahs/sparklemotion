package baaahs.ui.components

import baaahs.ui.Observable

class NestedListItem<T>(
    val item: T,
    children: List<NestedListItem<T>> = emptyList(),
    isOpen: Boolean = false,
    isSelected: Boolean = false,
    isFocused: Boolean = false
) : Observable() {
    var children = children
        set(value) { if (field != value) { field = value; notifyChanged() } }

    var isFocused = isFocused
        set(value) { if (field != value) { field = value; notifyChanged() } }

    var isSelected = isSelected
        set(value) { if (field != value) { field = value; notifyChanged() } }

    var isOpen = isOpen
        set(value) { if (field != value) { field = value; notifyChanged() } }
}