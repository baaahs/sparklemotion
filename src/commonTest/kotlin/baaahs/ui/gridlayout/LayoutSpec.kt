package baaahs.ui.gridlayout

import baaahs.describe
import org.spekframework.spek2.Spek

object LayoutSpec : Spek({
    describe<Layout> {
        val items by value {
            listOf(
                LayoutItem(0, 0, 1, 1, "A")
            )
        }
        val layout by value { Layout(items, 1, 1) }
        context("moveElement") {

        }
    }
})