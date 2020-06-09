package baaahs.app.ui

import baaahs.ui.Styles
import kotlinx.css.height
import kotlinx.css.pct
import styled.StyleSheet

object Styles : StyleSheet("AppUI", isStatic = true) {
    val fullHeight by Styles.css {
        height = 100.pct
    }
}
