package baaahs.ui.gridlayout

import kotlinx.css.Border
import kotlinx.css.BorderStyle
import kotlinx.css.Color
import kotlinx.css.Position
import kotlinx.css.backgroundColor
import kotlinx.css.border
import kotlinx.css.height
import kotlinx.css.pct
import kotlinx.css.position
import kotlinx.css.px
import kotlinx.css.*
import kotlinx.css.width
import mui.material.styles.Theme
import styled.StyleSheet
import baaahs.ui.transition
import kotlinx.css.bottom
import kotlinx.css.left
import kotlinx.css.properties.s
import kotlinx.css.right
import kotlinx.css.top

class Grid2Styles(val theme: Theme) : StyleSheet("app-ui-gridlayout", isStatic = true) {
    val gridRoot by css {
        position = Position.absolute
        backgroundColor = Color.Companion.darkGray
        border = Border(1.px, BorderStyle.solid, Color.Companion.black)
        width = 100.pct
        height = 100.pct
    }

    val gridItem by css {
        position = Position.absolute
//        backgroundColor = Color.Companion.pink
        border = Border(1.px, BorderStyle.solid, Color.Companion.black)
        display = Display.grid
        transition(::top, .2.s)
        transition(::bottom, .2.s)
        transition(::left, .2.s)
        transition(::right, .2.s)
    }

    val dragging by css {
        transition(::top, 0.s)
        transition(::bottom, 0.s)
        transition(::left, 0.s)
        transition(::right, 0.s)
    }

    val placeholder by css(/*gridItem*/) {
        position = Position.absolute
        display = Display.none
        outlineWidth = 4.px
        put("outlineStyle", "dashed")
        outlineColor = Color.orange // theme.palette.text.primary.asColor().withAlpha(.5)
        outlineOffset = (-6).px
        put("-webkit-transition", "-webkit-transform 0.2s")
        transition(::transform, 0.05.s)
    }
    val placeholderActive by css(/*gridItem*/) {
        display = Display.inherit
    }

    val debugBox by css {
        position = Position.fixed
        fontFamily = "monospace"
        fontSize = .6.rem
        whiteSpace = WhiteSpace.pre
        bottom = 0.px
        left = 0.px
        backgroundColor = Color.orange
        color = Color.black
        width = 600.px
        maxHeight = 90.em
        overflow = Overflow.auto
        zIndex = 1000
    }
}