package baaahs.app.ui.controls

import kotlinx.css.*
import styled.StyleSheet

object Styles : StyleSheet("UI-Gadgets", isStatic = true) {
    val horizontalButtonList by css {
        padding(2.px)

        descendants("button.MuiButtonBase-root") {
            width = 150.px
            height = 75.px
        }
    }

    val verticalButtonList by css {
        padding(2.px)

        descendants("button.MuiButtonBase-root") {
            width = 150.px
            height = 75.px
        }
    }

    val controlBox by css {
        height = 100.pct
    }
}