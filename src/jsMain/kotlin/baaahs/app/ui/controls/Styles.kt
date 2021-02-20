package baaahs.app.ui.controls

import baaahs.app.ui.ThemeStyles
import baaahs.ui.descendants
import baaahs.ui.name
import kotlinx.css.*
import kotlinx.css.properties.Timing
import kotlinx.css.properties.s
import kotlinx.css.properties.transition
import styled.StyleSheet

class Styles(
    private val appUiStyles: ThemeStyles
) : StyleSheet("app-ui-controls", isStatic = true) {
    val buttonGroupCard by css {
        display = Display.flex

        descendants(controlButton) {
            transition(::transform, duration = 0.25.s, timing = Timing.linear)
        }
    }

    val horizontalButtonList by css {
        padding(2.px)
        position = Position.relative
        whiteSpace = WhiteSpace.nowrap

        descendants("button.MuiButtonBase-root") {
            display = Display.block
        }
    }

    val verticalButtonList by css {
        padding(2.px)

        descendants("button.MuiButtonBase-root") {
        }
    }

    val editButton by css {
        opacity = 0
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

    val cardProblemBadge by css {
        position = Position.absolute
        right = .5.em
        top = .5.em
        zIndex = 1
        opacity = .75
    }

    val cardProblemInfo by css {
        color = Color.yellowGreen
    }

    val cardProblemWarning by css {
        color = Color.orange
    }

    val cardProblemError by css {
        color = Color.red
    }

    val visualizerCard by css {
        display = Display.flex
        flex(1.0, 0.0)
    }

    val dragHandle by css {
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

        transition(::transform, duration = 0.25.s, timing = Timing.linear)
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
        display = Display.grid
        position = Position.relative
        width = 150.px
        minHeight = 75.px

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
         ".${appUiStyles.editModeOff.name}" {
             ".${editButton.name}" {
                 put("opacity", "0 !important")
             }
             ".${dragHandle.name}" {
                 put("opacity", "0 !important")
             }
        }
    }
}