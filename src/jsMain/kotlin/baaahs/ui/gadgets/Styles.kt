package baaahs.ui.gadgets

import kotlinx.css.height
import kotlinx.css.px
import kotlinx.css.width
import styled.StyleSheet

object Styles : StyleSheet("UI.Gadgets", isStatic = true) {
    val buttons by css {
        width = 150.px
        height = 75.px
    }
}