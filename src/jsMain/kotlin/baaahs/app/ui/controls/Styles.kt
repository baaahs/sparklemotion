package baaahs.app.ui.controls

import baaahs.app.ui.StyleConstants
import baaahs.show.live.DataSourceOpenControl
import baaahs.ui.asColor
import baaahs.ui.descendants
import baaahs.ui.name
import kotlinx.css.*
import kotlinx.css.properties.Timing
import kotlinx.css.properties.border
import kotlinx.css.properties.s
import kotlinx.css.properties.transition
import mui.material.styles.Theme
import styled.StyleSheet

object Styles : StyleSheet("app-ui-controls", isStatic = true) {
    val editTransitionDuration = 0.25.s

    val buttonGroupCard by css {
        display = Display.flex
        overflowY = Overflow.scroll

        descendants(this@Styles, ::controlButton) {
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
        zIndex = StyleConstants.Layers.aboveSharedGlCanvas

        child("svg") {
            width = .75.em
            height = .75.em
        }
    }

    val cardProblemBadge by css {
        position = Position.absolute
        right = .5.em
        top = .5.em
        zIndex = StyleConstants.Layers.aboveSharedGlCanvas
        opacity = .75
        filter = "drop-shadow( 2px 2px 2px rgba(0, 0, 0, .7))"
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
        zIndex = StyleConstants.Layers.aboveSharedGlCanvas
    }

    val controlBox by css {
        display = Display.flex
        flex(1.0, 0.0)
        position = Position.relative
        marginRight = 0.em

        hover {
            child(".${editButton.name}") {
                opacity = .7
                filter = "drop-shadow(0px 0px 2px black)"
            }

            child(".${dragHandle.name}") {
                opacity = 1
                filter = "drop-shadow(0px 0px 2px black)"
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

    val beatLinkedSwitch by css {
        position = Position.absolute
        left = 0.px
        bottom = 0.px
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
        zIndex = StyleConstants.Layers.aboveSharedGlCanvas

        hover {
            child(".${editButton.name}") {
                opacity = .7
                filter = "drop-shadow(0px 0px 2px black)"
            }

            child(".${dragHandle.name}") {
                opacity = 1
                filter = "drop-shadow(0px 0px 2px black)"
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

    val inputLabel by css {
        whiteSpace = WhiteSpace.nowrap
    }


    val global = baaahs.ui.xCssBuilder().apply {
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

class ThemeStyles(val theme: Theme) : StyleSheet("app-ui-controls-theme", isStatic = true) {
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
        backgroundColor = theme.palette.secondary.main.asColor()
            .withAlpha(.125).blend(Color(theme.palette.background.paper))

        hover {
            backgroundColor = theme.palette.secondary.main.asColor()
                .withAlpha(.25).blend(Color(theme.palette.background.paper))
        }
    }

    val transitionHoldEngaged by css {
        color = theme.palette.secondary.contrastText.asColor()
        backgroundColor = theme.palette.secondary.main.asColor()

        hover {
            backgroundColor = theme.palette.secondary.main.asColor()
                .withAlpha(.75).blend(Color(theme.palette.background.paper))
        }
    }

    val speedButton by css {
        textTransform = TextTransform.none
    }

    val vacuityContainer by css {
        display = Display.flex
    }
}