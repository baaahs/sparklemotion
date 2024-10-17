package baaahs.sim.ui

import baaahs.app.ui.linearRepeating
import baaahs.ui.important
import baaahs.ui.rgba
import kotlinx.css.*
import kotlinx.css.properties.BoxShadow
import kotlinx.css.properties.lh
import mui.material.styles.Theme
import mui.system.Breakpoint
import styled.StyleSheet

class ThemedSimulatorStyles(val theme: Theme) : StyleSheet("sim-ui-themed", isStatic = true) {
    val menuBar by css {
        display = Display.flex
        justifyContent = JustifyContent.spaceBetween
        padding = Padding(8.px, 16.px)
        background = "#182128"
        declarations["box-shadow"] = "0px 1px 5px rgba(0, 0, 0, 0.4)"

        theme.breakpoints.down(Breakpoint.lg)() {
            padding = Padding(2.px)
        }
    }

    val global = CssBuilder().apply {
        ".mosaic-window-toolbar" {
            important(::display, Display.none)
        }
        ".mosaic-window-body" {
            important(::background, "unset")
        }
    }
}

object SimulatorStyles : StyleSheet("sim-ui", isStatic = true) {
    private val headerColor = Color("#f5a542")

    val app by css {
        display = Display.flex
        flexDirection = FlexDirection.column
        position = Position.absolute
        top = 0.px
        left = 0.px
        width = 100.pct
        height = 100.pct
    }

    val title by css {
        fontSize = 18.px
        color = Color.white
        userSelect = UserSelect.none
    }

    val launchButtonsContainer by css {
        padding = Padding(.25.em)
    }

    val panelToolbar by css {
        padding = Padding(0.px, 8.px)
    }

    val windowContainer by css {
        display = Display.flex
        alignItems = Align.stretch
        position = Position.absolute
        top = 0.px
        left = 0.px
        width = 100.pct
        height = 100.pct

        child("*") {
            position = Position.absolute
            top = 0.px
            left = 0.px
            width = 100.pct
            height = 100.pct
            display = Display.flex
            flexDirection = FlexDirection.column
            overflow = Overflow.hidden
        }
    }

    val modelSimulation by css {
        display = Display.flex
        flexDirection = FlexDirection.column

        header {
            background = linearRepeating(headerColor, headerColor.darken(15), 7.5.px)
            padding = Padding(1.5.px, .5.em)
            lineHeight = 1.25.em.lh
        }
    }

    val vizToolbar by css {
        zIndex = 10
        position = Position.absolute
        bottom = 0.em
        right = 0.em
        padding = Padding(0.px, 16.px)
    }

    val unstarted by css(baaahs.app.ui.controls.Styles.visualizerWarning) {
        right = .5.em
        zIndex = 10
        justifyContent = JustifyContent.center

        button {
            color = Color.white
        }
    }

    val showPinkyConsoleButton by css {
        color = Color.white
        backgroundColor = Color.darkRed.withAlpha(.5)
        borderRadius = 3.px
        margin = Margin(1.em)
        padding = Padding(.5.em)
    }

    val vizWarning by css(baaahs.app.ui.controls.Styles.visualizerWarning) {
        zIndex = 10
        svg {
            color = Color.white
        }
    }

    val statusPanel by css {
        display = Display.flex

        header {
            background = linearRepeating(headerColor, headerColor.darken(15), 7.5.px)
            padding = Padding(1.5.px, .5.em)
            lineHeight = 1.25.em.lh
        }
    }

    val statusPanelToolbar by css {
        padding = Padding(1.em)
    }

    val consoleContainer by css {
        flex = Flex(1)
        overflow = Overflow.scroll
        fontSize = 11.px
    }

    val section by css {
        display = Display.block
        borderWidth = 1.px
        borderStyle = BorderStyle.dashed
        borderColor = Color.gray
    }

    val networkPacketLossRate by css {
        backgroundColor = Color.blanchedAlmond
        display = Display.inlineBlock
        borderWidth = 2.px
        borderStyle = BorderStyle.dotted
        padding = Padding(2.px)
    }

    val selection by css {
        paddingBottom = 3.em
    }

    private val subsection by css {
        border = Border(2.px, BorderStyle.solid, Color.gray)
        borderRadius = 0.5.em
    }

    val bpmDisplay by css {
        bottom = 20.px
        padding = Padding(20.px)
        color = Color("#ffaaff")
        fontFamily = "'Press Start 2P', sans-serif"
        fontSize = 10.pt
        fontStyle = FontStyle.normal
        width = 100.pct
        put("text-shadow", "1px 1px 3px black, -1px -1px 3px black")
    }

    val beatOn by css {
        color = Color("#ff88ff")
    }

    val brainIndicator by css {
        margin = Margin(1.px)
        display = Display.inlineBlock
        width = 10.px
        height = 10.px
        border = Border(1.px, BorderStyle.solid, Color.black)

        hover {
            cursor = Cursor.pointer
            border = Border(1.px, BorderStyle.solid, Color.white)
        }

        child(".unknown") { backgroundColor = Color.darkGray }
        child(".link") { backgroundColor = Color.orange }
        child(".online") { backgroundColor = Color.orange }
    }

    val brainStateBooting by css { backgroundColor = Color.red}
    val brainStateLinked by css { backgroundColor = Color.yellow }
    val brainStateHello by css { backgroundColor = Color.orange}
    val brainStateMapped by css { backgroundColor = Color.lightBlue }
    val brainStateShading by css { backgroundColor = Color.lightGreen }
    val brainStateRebooting by css { backgroundColor = Color.mediumPurple}
    val brainStateStopped by css { backgroundColor = Color.gray}

    val dataWithUnit by css {
        textAlign = TextAlign.right
    }

    val fakeClientDevicePad by css {
        zIndex = 1000
        position = Position.absolute
        border = Border(1.px, BorderStyle.solid, Color("#404040"))
        borderRadius = 8.px
        backgroundImage = Image("linear-gradient(to bottom right, #eee, #fff)")
        cursor = Cursor.grab
        padding = Padding(28.px, 40.px, 28.px, 28.px)
        color = Color.white
        right = 5.px
        bottom = 5.px
        boxShadow += BoxShadow(rgba(0, 0, 0, 0.25), 4.px, 5.px, 2.px, 2.px)
    }

    val fakeClientDeviceControls by css {
        position = Position.absolute
        fontSize = 14.px
        top = 0.px
        padding = Padding(4.px)
        right = 0.px
        color = Color("#222")
        zIndex = 1

        button {
            color = Color("#222222")
            padding = Padding(4.px)
            minWidth = 0.px
            cursor = Cursor.pointer

            svg {
                fontSize = 1.25.rem
            }
            hover {
                color = Color("#e3e3e3")
            }
        }
    }

    val fakeClientDeviceHomeButton by css {
        position = Position.absolute
        width = 18.px
        height = 18.px
        right = 6.px
        top = 50.pct
        border = Border(1.px, BorderStyle.solid, Color.black)
        borderRadius = 50.pct
        backgroundImage = Image("radial-gradient(#fff, #ddd)")
        cursor = Cursor.pointer

        hover {
            backgroundImage = Image("radial-gradient(#ddd, #aaa)")
        }
    }
    
    val fakeClientDeviceContent by css {
        overflow = Overflow.hidden
        cursor = Cursor.default
        position = Position.relative
        width = LinearDimension.auto
        height = LinearDimension.auto
        padding = Padding(2.px)
        border = Border(2.px, BorderStyle.solid, Color("#373737"))
        backgroundColor = Color("#4F4F4F")

        nav {
            marginBottom = 8.px
        }
    }
}