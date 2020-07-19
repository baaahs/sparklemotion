package baaahs.app.ui

import baaahs.ui.*
import kotlinx.css.*
import kotlinx.css.properties.*
import materialui.styles.mixins.toolbar
import materialui.styles.muitheme.MuiTheme
import materialui.styles.transitions.create
import materialui.styles.transitions.sharp
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

class ThemeStyles(val theme: MuiTheme) : StyleSheet("app-ui-theme", isStatic = true) {
    private val drawerWidth = 260.px

    private val drawerClosedShift = partial {
        left = 0.px
        theme.transitions.create("left") {
            easing = theme.transitions.easing.sharp
            duration = theme.transitions.duration.enteringScreen
        }
    }

    private val drawerOpenShift = partial {
        left = drawerWidth
        transition = theme.transitions.create("left") {
            easing = theme.transitions.easing.sharp
            duration = theme.transitions.duration.leavingScreen
        }
    }

    val appDrawerOpen by css {}
    val appDrawerClosed by css {}

    val title by css { }

    val appToolbar by css {
        height = 40.px
        mixIn(theme.mixins.toolbar)

        descendants(title) {
            flexGrow = 1.0
        }

        within(appDrawerOpen) { mixIn(drawerOpenShift) }
        within(appDrawerClosed) { mixIn(drawerClosedShift) }
    }

    val appContent by css {
        display = Display.flex
        flexDirection = FlexDirection.column
        position = Position.absolute
        top = 3.em
        left = 0.px
        width = 100.pct
        height = 100.pct - 3.em

        within(appDrawerOpen) { mixIn(drawerOpenShift) }
        within(appDrawerClosed) { mixIn(drawerClosedShift) }
    }

    val appDrawer by css {
        position = Position.absolute
        width = drawerWidth
        height = 100.pct
        flexShrink = 0.0
    }

    val appDrawerPaper by css {
//        position = Position.relative
        put("position", "relative !important")
        width = drawerWidth
    }

    val appDrawerHeader by css {
        display = Display.flex
        alignItems = Align.center
        padding = theme.spacing(0, 1)
        rules.addAll(theme.mixins.toolbar.rules)
        theme.mixins.toolbar
        justifyContent = JustifyContent.flexEnd
    }
}

object Styles : StyleSheet("app-ui", isStatic = true) {
    val root by css {
        display = Display.flex
    }

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
        transition(StyledElement::opacity, duration = 1.s)

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
            background = linearRepeating(Color.lightPink.withAlpha(.5), Color.lightPink.withAlpha(.25))
        }

        descendants(sceneControls) {
            background = linearRepeating(Color.lightGreen.withAlpha(.5), Color.lightGreen.withAlpha(.25))
        }

        descendants(patchControls) {
            background = linearRepeating(Color.lightBlue.withAlpha(.5), Color.lightBlue.withAlpha(.25))
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
        ".${editModeOn.name}.${unplacedControlsPalette.name}" {
            opacity = 1
        }
    }
}
