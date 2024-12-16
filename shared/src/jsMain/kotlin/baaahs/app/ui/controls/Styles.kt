package baaahs.app.ui.controls

import baaahs.SparkleMotion
import baaahs.app.ui.StyleConstants
import baaahs.show.live.FeedOpenControl
import baaahs.ui.*
import kotlinx.css.*
import kotlinx.css.properties.Timing
import kotlinx.css.properties.lh
import kotlinx.css.properties.s
import mui.material.styles.Theme
import styled.StyleSheet

object Styles : StyleSheet("app-ui-controls", isStatic = true) {
    val editTransitionDuration = 0.25.s

    val horizontalButtonList by css {
        padding = Padding(2.px)
        position = Position.relative
        whiteSpace = WhiteSpace.nowrap

        descendants("button.MuiButtonBase-root") {
            display = Display.block
        }
    }

    val verticalButtonList by css {
        padding = Padding(2.px)

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
        flex = Flex(1.0, 0.0)
    }

    val visualizerMenuAffordance by css {
        position = Position.absolute
        padding = Padding(2.px)
        borderRadius = 3.px
        backgroundColor = Color.white.withAlpha(.5)
        width = 2.em
        height = 2.em
        right = .5.em
        bottom = .5.em
    }

    val visualizerWarning by css {
        display = Display.flex
        lineHeight = 1.em.lh
        alignItems = Align.center
        position = Position.absolute
        padding = Padding(2.px, .5.em)
        borderRadius = 3.px
        backgroundColor = Color.darkRed.withAlpha(.5)
        left = .5.em
        bottom = .5.em

        svg { // Margin on the (?) help icon.
            marginLeft = .3.em
        }
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
        flex = Flex(0.0, 0.0, FlexBasis.auto)
        position = Position.relative
        marginRight = 0.em
        minWidth = 5.em
        minHeight = 5.em

        hover {
            child(this@Styles, ::editButton) {
                opacity = .7
                filter = "drop-shadow(0px 0px 2px black)"
            }

            child(this@Styles, ::dragHandle) {
                opacity = 1
                filter = "drop-shadow(0px 0px 2px black)"
                cursor = Cursor.move
            }
        }

        transition(::transform, duration = editTransitionDuration, timing = Timing.linear)
    }

    val beatLinkedSwitch by css {
        position = Position.absolute.important
        left = 0.px
        bottom = 0.px
    }

    val resetSwitch by css {
        position = Position.absolute
        right = 0.px
        bottom = 0.px
    }

    val inUse by css {
//        position = Position.relative
    }

    val notInUse by css {
        opacity = .75
    }

    val notExplicitlySized by css {}

    val controlRoot by css {}

    val controlButton by css {
        display = Display.grid
        position = Position.absolute
        top = 0.px
        left = 0.px
        bottom = 0.px
        right = 0.px
        zIndex = StyleConstants.Layers.aboveSharedGlCanvas

        hover {
            child(this@Styles, ::editButton) {
                opacity = .7
                filter = "drop-shadow(0px 0px 2px black)"
            }

            child(this@Styles, ::dragHandle) {
                opacity = 1
                filter = "drop-shadow(0px 0px 2px black)"
                cursor = Cursor.move
            }
        }

        button {
            minWidth = 0.px
            minHeight = 0.px
        }
    }

    val buttonShaderPreviewContainer by css {
        position = Position.absolute
        left = 0.px
        top = 0.px
        width = 100.pct
        height = 100.pct
    }

    val buttonControl by css {
        alignItems = Align.end.important
        lineHeight = .95.em.lh.important
        border = Border(5.px, BorderStyle.dotted, Color.transparent).important
        div {
            marginBottom = (-.33).em.important
        }
    }
    val buttonControlWithoutPreview by css {}
    val buttonControlWithPreview by css {
        color = Color.white.important
        put("textShadow", "-1px 0px 2px black, 0px -1px 2px black, 1px 0px 2px black, 0px 1px 2px black")
        fontWeight = "calc((1 - var(--dimmer-level)) * 600 + 100)".important.unsafeCast<FontWeight>()
        backgroundColor = Color.transparent.important
    }

    val buttonControlSelectedWithPreview by css {
        color = Color.white.important
        put("textShadow", "-1px 0px 2px black, 0px -1px 2px black, 1px 0px 2px black, 0px 1px 2px black")
        border = Border(5.px, BorderStyle.dotted, Color.orange).important
        backgroundColor = Color.transparent.important
    }

    val inputLabel by css {
        left = (-.7).em
    }

    val slider by css {}
    val deviceChannelNumber by css {
        position = Position.absolute
        fontSize = 0.9.em
        fontWeight = FontWeight.bold
        top = 3.px
        right = 4.px
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

val FeedOpenControl.inUseStyle get() = if (this.inUse) Styles.inUse else Styles.notInUse

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

    val feedTitle by css {
        fontWeight = FontWeight.w500
        display = Display.block
        position = Position.absolute
        top = 0.5.em
        left = 1.5.px
        color = theme.paperHighContrast
//        put("text-shadow", "1px 1px 3px black, -1px -1px 3px black")
        declarations["writing-mode"] = "vertical-lr"
        userSelect = UserSelect.none
        pointerEvents = PointerEvents.none
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
        alignContent = Align.stretch
        overflow = Overflow.scroll
        borderWidth = .25.em
        borderStyle = "inset".asDynamic()
        borderColor = theme.paperMediumContrast
        borderRadius = 10.px
        padding = Padding(.25.em)
        background = "linear-gradient(" +
                "-185deg, " +
                "${theme.paperLowContrast}, " +
                "${theme.palette.background.paper}, " +
                "${theme.paperLowContrast}" +
                ")"
    }

    val buttonGroupCard by css {
        display = Display.flex
        flexDirection = FlexDirection.column
        overflowY = Overflow.scroll
        backgroundColor = if (SparkleMotion.USE_CSS_TRANSFORM) {
            Color("#0000007f")
        } else {
            theme.paperLowContrast
        }

        descendants(Styles, Styles::controlButton) {
            transition(::transform, duration = Styles.editTransitionDuration, timing = Timing.linear)
        }
    }
}