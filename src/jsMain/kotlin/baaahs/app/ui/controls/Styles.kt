package baaahs.app.ui.controls

import baaahs.ui.getName
import kotlinx.css.*
import kotlinx.css.properties.Timing
import kotlinx.css.properties.s
import kotlinx.css.properties.transition
import styled.StyleSheet

object Styles : StyleSheet("app-ui-controls", isStatic = true) {
    val horizontalButtonList by css {
        padding(2.px)
        position = Position.relative
        whiteSpace = WhiteSpace.nowrap

        descendants("button.MuiButtonBase-root") {
            width = 150.px
            height = 75.px
            display = Display.block
        }
    }

    val verticalButtonList by css {
        padding(2.px)

        descendants("button.MuiButtonBase-root") {
            width = 150.px
            height = 75.px
        }
    }

    val dragHandle by css {
        opacity = 0
        transition(StyledElement::visibility, duration = 0.25.s, timing = Timing.linear)
        position = Position.absolute
        right = 2.px
        bottom = (-2).px
        zIndex = 1
    }

    val controlBox by css {
        position = Position.relative
        backgroundColor = Color.white.withAlpha(.5)
        border = "1px solid black"
        borderRadius = 3.px

        hover {
            child(".${dragHandle.getName()}") {
                opacity = 1
            }
        }
    }

    val controlButton by css {
        position = Position.relative

        hover {
            child(".${dragHandle.getName()}") {
                opacity = 1
            }
        }
    }

    val global = CSSBuilder().apply {
         ".${baaahs.app.ui.Styles.editModeOff.getName()}" {
             ".${dragHandle.getName()}" {
                 put("opacity", "0 !important")
             }
        }
    }
}