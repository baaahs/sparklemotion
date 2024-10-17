package baaahs.ui.diagnostics

import kotlinx.css.*
import mui.material.styles.Theme
import styled.StyleSheet

class DiagnosticsStyles(private val theme: Theme) : StyleSheet("ui-diagnostics-diagram", isStatic = true) {
    val contentDiv by css {
        overflow = Overflow.scroll
    }

    val table by css {
        width = 100.pct

        th {
            textAlign = TextAlign.start
        }
    }

    val incomingLinkRow by css {
        th {
            paddingLeft = 1.em
        }
    }
}