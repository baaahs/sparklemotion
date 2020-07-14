package baaahs.app.ui

import baaahs.ui.descendants
import kotlinx.css.*
import kotlinx.css.properties.Angle
import kotlinx.css.properties.deg
import kotlinx.css.properties.s
import kotlinx.css.properties.transition
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

object Styles : StyleSheet("AppUI", isStatic = true) {
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
        display = Display.inlineBlock
        position = Position.relative
        height = 100.pct
        verticalAlign = VerticalAlign.top
    }

    val showControls by css {
    }

    val sceneControls by css {
    }

    val patchControls by css {
    }

    val controlPanelHelpText by css {
        display = Display.none
    }

    val editModeOn by css {
        descendants(layoutControls) {
            padding(1.em)
            minWidth = 3.em; transition(StyledElement::minWidth)
            minHeight = 3.em; transition(StyledElement::minHeight)
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
            left = 1.px
            declarations["writing-mode"] = "vertical-lr"
        }
    }

    val editModeOff by css {
        descendants(layoutControls) {
            transition(duration = 0.5.s)
        }
    }
}
