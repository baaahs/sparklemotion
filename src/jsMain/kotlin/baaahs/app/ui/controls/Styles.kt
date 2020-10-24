package baaahs.app.ui.controls

import baaahs.ui.name
import kotlinx.css.*
import kotlinx.css.properties.Timing
import kotlinx.css.properties.s
import kotlinx.css.properties.transition
import styled.StyleSheet

object Styles : StyleSheet("app-ui-controls", isStatic = true) {
    val showControls by css {
        display = Display.flex
    }

    val buttonGroupCard by css {
        display = Display.flex
    }

    val horizontalButtonList by css {
        padding(2.px)
        position = Position.relative
        whiteSpace = WhiteSpace.nowrap

        descendants("button.MuiButtonBase-root") {
            width = 150.px
            minHeight = 75.px
            display = Display.block
        }
    }

    val verticalButtonList by css {
        padding(2.px)

        descendants("button.MuiButtonBase-root") {
            width = 150.px
            minHeight = 75.px
        }
    }

    val editButton by css {
        opacity = 0
        backgroundColor = Color.white // TODO: should be based on theme.
        transition(::opacity, duration = 0.25.s, timing = Timing.linear)
        position = Position.absolute
        right = 2.px
        bottom = (-2).px + 2.em
        zIndex = 1

        child("svg") {
            width = .75.em
            height = .75.em
        }
    }

    val visualizerCard by css {
        display = Display.flex
        flex(1.0, 0.0)
    }

    val dragHandle by css {
        backgroundColor = Color.white // TODO: should be based on theme.
        opacity = 0
        transition(::opacity, duration = 0.25.s, timing = Timing.linear)
        position = Position.absolute
        right = 2.px
        bottom = (-2).px
        zIndex = 1
    }

    val controlBox by css {
        display = Display.flex
        flex(1.0, 0.0)
        position = Position.relative
        marginRight = 0.em

        hover {
            child(".${editButton.name}") {
                opacity = .7
            }

            child(".${dragHandle.name}") {
                opacity = 1
                cursor = Cursor.move
            }
        }
    }

    val dataSourceTitle by css {
        fontWeight = FontWeight.w500
        display = Display.block
        position = Position.absolute
        top = 0.5.em
        left = 0.5.px
        declarations["writing-mode"] = "vertical-lr"
    }

    val dataSourceLonelyTitle by css {
        fontWeight = FontWeight.bold
    }

    val controlButton by css {
        display = Display.flex
        position = Position.relative

        hover {
            child(".${editButton.name}") {
                opacity = .7
            }

            child(".${dragHandle.name}") {
                opacity = 1
                cursor = Cursor.move
            }
        }
    }

    val global = CSSBuilder().apply {
         ".${baaahs.app.ui.Styles.editModeOff.name}" {
             ".${editButton.name}" {
                 put("opacity", "0 !important")
             }
             ".${dragHandle.name}" {
                 put("opacity", "0 !important")
             }
        }
    }
}