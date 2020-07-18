package baaahs.app.ui

import baaahs.ui.descendants
import baaahs.ui.getName
import kotlinx.css.*
import kotlinx.css.properties.*
import styled.StyleSheet

private fun linearRepeating(
    color1: Color,
    color2: Color,
    interval: LinearDimension = 10.px,
    angle: Angle = (-45).deg
): String {
    return """
        repeating-linear-gradient(
            $angle,
            $color1,
            $color1 $interval,
            $color2 $interval,
            $color2 ${interval.times(2)}
        );

    """.trimIndent()
}

object Styles : StyleSheet("app-ui", isStatic = true) {
    val layoutPanel by css {
        height = 100.pct
    }

    val fullHeight by css {
        height = 100.pct
    }

    val buttons by css {
        backgroundColor = Color.white.withAlpha(.75)
    }

    val layoutControls by css {
        display = Display.inlineFlex
        position = Position.relative
        height = 100.pct
        verticalAlign = VerticalAlign.top

        transition(StyledElement::minWidth, duration = .5.s)
        transition(StyledElement::minHeight, duration = .5.s)
    }

    val showControls by css {
    }

    val sceneControls by css {
    }

    val patchControls by css {
    }

    val unplacedControlsPalette by css {
        position = Position.fixed
        left = 5.em
        bottom = 5.em
        zIndex = 100
        opacity = 0
        display = Display.none

        transition(StyledElement::opacity, duration = 1.s)
        transition(StyledElement::display, delay = 1.s)

        hover {
            descendants(dragHandle) {
                opacity = 1
            }
        }

        descendants(baaahs.app.ui.controls.Styles.controlBox) {
            marginBottom = 0.25.em
        }
    }

    val unplacedControlsPaper by css {
        padding(1.em)
    }

    val unplacedControlsDroppable by css {
        overflowY = Overflow.scroll
        minHeight = 4.em
        height = 33.vh
    }

    val controlPanelHelpText by css {
        display = Display.none
    }

    val dragHandle by css {
        opacity = .2
        transition(StyledElement::visibility, duration = 0.25.s, timing = Timing.linear)
        position = Position.absolute
        right = 2.px
        top = 0.5.em
        zIndex = 101
    }

    val editModeOn by css {
        descendants(layoutControls) {
            padding(1.em)
            minWidth = 5.em
            minHeight = 5.em
            border = "1px solid black"
            borderRadius = 3.px

            transition(duration = 0.5.s)
        }

        descendants(showControls) {
            background = linearRepeating(Color.lightPink, Color.lightPink.lighten(20))
        }

        descendants(sceneControls) {
            background = linearRepeating(Color.lightGreen, Color.lightGreen.lighten(20))
        }

        descendants(patchControls) {
            background = linearRepeating(Color.lightBlue, Color.lightBlue.lighten(20))
        }

        descendants(controlPanelHelpText) {
            display = Display.block
            position = Position.absolute
            top = 0.5.em
            left = 0.5.px
            declarations["writing-mode"] = "vertical-lr"
        }

        descendants(baaahs.app.ui.controls.Styles.controlBox) {
            padding(3.px)
            marginBottom = 0.25.em
            backgroundColor = Color.white.withAlpha(.5)
            border(
                width = 1.px,
                style = BorderStyle.solid,
                color = Color.black.withAlpha(.5),
                borderRadius = 3.px
            )
        }

        descendants(baaahs.app.ui.controls.Styles.dragHandle) {
            opacity = .2
        }
    }

    val editModeOff by css {
        descendants(layoutControls) {
            transition(duration = 0.5.s)
        }

        descendants(unplacedControlsPalette) {
            opacity = 0;
            display = Display.none;
        }
    }

    val global = CSSBuilder().apply {
        ".${editModeOn.getName()}.${unplacedControlsPalette.getName()}" {
            display = Display.block
            opacity = 1
        }
    }
}
