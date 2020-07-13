package baaahs.ui.gadgets

import kotlinx.css.height
import kotlinx.css.padding
import kotlinx.css.px
import kotlinx.css.width
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
}