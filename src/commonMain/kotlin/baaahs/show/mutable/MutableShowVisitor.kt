package baaahs.show.mutable

import baaahs.show.Feed
import baaahs.show.Surfaces

interface MutableShowVisitor {
    fun visit(mutablePatchHolder: MutablePatchHolder) {}
    fun visit(mutablePatch: MutablePatch) {}
    fun visit(surfaces: Surfaces) {}
    fun visit(mutableControl: MutableControl) {}
    fun visit(mutableShader: MutableShader) {}
    fun visit(mutableStream: MutableStream) {}
    fun visit(feed: Feed) {}

    fun visit(layout: MutableLayout) {
        layout.tabs.forEach { tab -> visit(tab) }
    }

    fun visit(tab: MutableTab) {
        when (tab) {
            is MutableGridTab -> {
                visit(tab)
            }
        }
    }

    fun visit(gridTab: MutableGridTab) {
        gridTab.items.forEach { visit(it) }
    }

    fun visit(gridItem: MutableGridItem) {
        visit(gridItem.control)
    }
}