package baaahs.ui.gridlayout

import baaahs.ui.descendants
import baaahs.ui.important
import baaahs.ui.selector
import baaahs.ui.transition
import kotlinx.css.*
import kotlinx.css.Position
import kotlinx.css.properties.s
import mui.material.styles.Theme
import styled.StyleSheet

class GridManagerStyles(val theme: Theme) : StyleSheet("app-ui-gridlayout", isStatic = true) {
    val gridRoot by css {
        position = Position.absolute
        backgroundColor = Color.Companion.darkGray
        border = Border(1.px, BorderStyle.solid, Color.Companion.black)
        width = 100.pct
        height = 100.pct
    }

    val gridItem by css {
        position = Position.absolute
//        zIndex = StyleConstants.Layers.aboveSharedGlCanvas
//        backgroundColor = Color.Companion.pink
//        border = Border(3.px, BorderStyle.solid, Color.Companion.transparent)
        display = Display.grid
        transition(::top, .2.s)
        transition(::bottom, .2.s)
        transition(::left, .2.s)
        transition(::right, .2.s)
    }

    val gridOuterContainer by css {
        position = Position.absolute
        width = 100.pct
        height = 100.pct
        userSelect = UserSelect.none
        declarations["-webkit-user-select"] = "none" // Disable text selection.
        declarations["-webkit-touch-callout"] = "none" // Disable the callout menu.
    }

    val gridEmptyCells by css {}

    val gridContainer by css {
//        position = Position.absolute
//        backgroundColor = Color.Companion.pink
//        border = Border(1.px, BorderStyle.solid, Color.Companion.black)
        display = Display.grid
//        transition(::top, .2.s)
//        transition(::bottom, .2.s)
//        transition(::left, .2.s)
//        transition(::right, .2.s)
    }

    val gridResizeHandleTopLeft by css {
        position = Position.absolute
        left = 0.px
        top = 0.px
        width = 40.px
        height = 40.px
        zIndex = 100
        opacity = 0
        pointerEvents = PointerEvents.none
    }

    val gridResizeHandleBottomRight by css {
        position = Position.absolute
        right = 0.px
        bottom = 0.px
        width = 40.px
        height = 40.px
        zIndex = 100
        opacity = 0
        transition(::opacity, .25.s)
        pointerEvents = PointerEvents.none
    }

    val editing by css {
        child(selector(::gridResizeHandleTopLeft)) {
            opacity = 1
            pointerEvents = PointerEvents.auto
        }
        child(selector(::gridResizeHandleBottomRight)) {
            opacity = 1
            pointerEvents = PointerEvents.auto
        }
    }

    val notYetLayedOut by css {
        visibility = Visibility.hidden
    }

    val dragging by css {
        transition(::top, 0.s)
        transition(::bottom, 0.s)
        transition(::left, 0.s)
        transition(::right, 0.s)
    }

    val resizing by css {
        transition(::top, 0.s)
        transition(::bottom, 0.s)
        transition(::left, 0.s)
        transition(::right, 0.s)
    }

    val disableTransitions by css {
        descendants(this@GridManagerStyles, ::gridItem) {
            transition(::top, 0.s).important
            transition(::bottom, 0.s).important
            transition(::left, 0.s).important
            transition(::right, 0.s).important
        }
    }

    val placeholder by css(/*gridItem*/) {
        position = Position.absolute
        display = Display.none
        outlineWidth = 4.px
        outlineStyle = OutlineStyle.dashed
        outlineColor = Color("#9900FF") // theme.palette.text.primary.asColor().withAlpha(.5)
        outlineOffset = (-6).px
        put("-webkit-transition", "-webkit-transform 0.2s")
        transition(::transform, 0.05.s)
    }
    val placeholderActive by css(/*gridItem*/) {
        display = Display.inherit
    }
}