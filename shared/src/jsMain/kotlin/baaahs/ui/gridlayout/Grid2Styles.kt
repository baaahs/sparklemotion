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
import kotlinx.css.transition
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
        backgroundColor = Color.Companion.lightPink
        border = Border(1.px, BorderStyle.solid, Color.Companion.black)
        width = 100.pct
        height = 100.pct
    }

    val gridItem by css {
        position = Position.absolute
        backgroundColor = Color.Companion.pink
        border = Border(1.px, BorderStyle.solid, Color.Companion.black)
        transition(::top, .2.s)
        transition(::bottom, .2.s)
        transition(::left, .2.s)
        transition(::right, .2.s)
    }
}