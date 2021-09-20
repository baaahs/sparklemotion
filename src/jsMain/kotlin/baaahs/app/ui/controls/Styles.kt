package baaahs.app.ui.controls

import baaahs.show.live.DataSourceOpenControl
import baaahs.ui.descendants
import baaahs.ui.name
import kotlinx.css.*
import kotlinx.css.properties.Timing
import kotlinx.css.properties.border
import kotlinx.css.properties.s
import kotlinx.css.properties.transition
import materialui.styles.muitheme.MuiTheme
import materialui.styles.palette.contrastText
import materialui.styles.palette.main
import materialui.styles.palette.paper
import styled.StyleSheet

object Styles : StyleSheet("app-ui-controls", isStatic = true) {
    val editTransitionDuration = 0.25.s

    val buttonGroupCard by css {
        display = Display.flex
        backgroundColor = Color.transparent
        overflowY = Overflow.scroll

        descendants(controlButton) {
            transition(::transform, duration = editTransitionDuration, timing = Timing.linear)
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
        display = Display.none
        transition(::display, duration = 0.s)
        opacity = 0
        transition(::opacity, duration = editTransitionDuration, timing = Timing.linear)
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
        display = Display.none
        transition(::display, duration = 0.s)
        opacity = 0
        transition(::opacity, duration = editTransitionDuration, timing = Timing.linear)
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
        backgroundColor = Color.transparent

        hover {
            child(".${editButton.name}") {
                opacity = .7
            }

            child(".${dragHandle.name}") {
                opacity = 1
                cursor = Cursor.move
            }
        }

        transition(::transform, duration = editTransitionDuration, timing = Timing.linear)
    }

    val dataSourceTitle by css {
        fontWeight = FontWeight.w500
        display = Display.block
        position = Position.absolute
        top = 0.5.em
        left = 0.5.px
        declarations["writing-mode"] = "vertical-lr"
        userSelect = UserSelect.none
        pointerEvents = PointerEvents.none
    }

    val dataSourceLonelyTitle by css {
        fontWeight = FontWeight.bold
    }

    val inUse by css {
    }

    val notInUse by css {
        opacity = .75
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

    val buttonShaderPreviewContainer by css {
        position = Position.absolute
        left = 0.px
        top = 0.px
        width = 100.pct
        height = 100.pct
    }

    val buttonLabelWhenPreview by css {
        color = Color.black
        background = "radial-gradient(rgba(255,255,255,.8), transparent)"
    }

    val buttonSelectedWhenPreview by css {
        border(5.px, BorderStyle.solid, Color.orange.withAlpha(.75))
        background = "radial-gradient(rgba(255,255,255,.8), transparent)"
    }

    val global = CssBuilder().apply {
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

val DataSourceOpenControl.inUseStyle get() = if (this.inUse) Styles.inUse else Styles.notInUse

class ThemeStyles(val theme: MuiTheme) : StyleSheet("app-ui-controls-theme", isStatic = true) {
    val static = Styles

    val transitionCard by css {
        display = Display.grid
        gridTemplateColumns = GridTemplateColumns.auto
        gridTemplateRows = GridTemplateRows("auto auto auto 1fr")
        gridTemplateAreas = GridTemplateAreas("""
            "speed speed fader"
            "shape shape fader"
            "effect effect fader"
            "hold go fader"
        """)
        gap = .25.em

//        header {
//            position = Position.absolute
//            right = 1.em
//            top = 0.px
//            transform { rotate((-90).deg) }
//            declarations["transformOrigin"] = "top right"
//        }
    }

    val transitionHoldButton by css {
        backgroundColor = theme.palette.secondary.main
            .withAlpha(.125).blend(theme.palette.background.paper)

        hover {
            backgroundColor = theme.palette.secondary.main
                .withAlpha(.25).blend(theme.palette.background.paper)
        }
    }

    val transitionHoldEngaged by css {
        color = theme.palette.secondary.contrastText
        backgroundColor = theme.palette.secondary.main

        hover {
            backgroundColor = theme.palette.secondary.main
                .withAlpha(.75).blend(theme.palette.background.paper)
        }
    }

    val speedButton by css {
        textTransform = TextTransform.none
    }

    val vacuityContainer by css {
        display = Display.flex
    }
}