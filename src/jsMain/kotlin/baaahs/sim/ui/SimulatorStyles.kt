package baaahs.sim.ui

import baaahs.ui.important
import kotlinx.css.*
import kotlinx.css.properties.border
import kotlinx.css.properties.boxShadow
import mui.material.styles.Theme
import mui.system.Breakpoint
import styled.StyleSheet

class ThemedSimulatorStyles(val theme: Theme) : StyleSheet("sim-ui-themed", isStatic = true) {
    val menuBar by css {
        display = Display.flex
        justifyContent = JustifyContent.spaceBetween
        padding = "8px 16px"
        background = "#182128"
        declarations["box-shadow"] = "0px 1px 5px rgba(0, 0, 0, 0.4)"

        theme.breakpoints.down(Breakpoint.lg)() {
            padding = "2px"
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

    val menu by css {
    }

    val panelToolbar by css {
        padding = "0 8px"
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
    }

    val vizToolbar by css {
        flex(0.0, 0.0, FlexBasis.auto)
        padding = "0 16px"
        justifyContent = JustifyContent.spaceBetween
        display = Display.flex
    }

    val statusPanelToolbar by css {}

    val console by css {
        color = Color.black
        backgroundColor = Color.white
        bottom = 5.px
        right = 5.px
        padding(.25.em)
        fontSize = 10.pt
        fontFamily = "Helvetica"

        width = 100.pct
        height = 100.pct
        overflow = Overflow.scroll
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
        padding(2.px)
    }

    private val subsection by css {
        border(2.px, BorderStyle.solid, Color.gray, 0.5.em)
    }

    val bpmDisplay by css {
        bottom = 20.px
        padding(20.px)
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
        margin(1.px)
        display = Display.inlineBlock
        width = 8.px
        height = 8.px
        border(1.px, BorderStyle.solid, Color.black)

        hover {
            cursor = Cursor.pointer
            backgroundColor = Color("#ff7600")
        }

        child(".unknown") { backgroundColor = Color.darkGray }
        child(".link") { backgroundColor = Color.orange }
        child(".online") { backgroundColor = Color.orange }
    }

    val dataWithUnit by css {
        textAlign = TextAlign.right
    }

    val fakeClientDevicePad by css {
        zIndex = 1000
        position = Position.absolute
        border = "1px solid #404040"
        borderRadius = 8.px
        backgroundImage = Image("linear-gradient(to bottom right, #eee, #fff)")
        cursor = Cursor.grab
        padding = "28px 40px 28px 28px"
        color = Color.white
        right = 5.px
        bottom = 5.px
        boxShadow(rgba(0, 0, 0, 0.25), 4.px, 5.px, 2.px, 2.px)
    }
    
    val fakeClientDeviceControls by css {
        position = Position.absolute
        fontSize = 14.px
        top = 0.px
        padding = "4px"
        right = 0.px
        color = Color("#222")
        zIndex = 1
    }
    val fakeClientDeviceIconButton by css {
        color = Color("#222222")
        padding = "4px"
        marginLeft = 8.px
        cursor = Cursor.pointer

        hover {
            color = Color("#e3e3e3")
        }
    }

    val fakeClientDeviceHomeButton by css {
        position = Position.absolute
        width = 18.px
        height = 18.px
        right = 6.px
        top = 50.pct
        border = "1px solid black"
        borderRadius = 50.pct
        backgroundImage = Image("radial-gradient(#fff, #ddd)")
    }
    
    val fakeClientDeviceContent by css {
        overflow = Overflow.hidden
        cursor = Cursor.default
        position = Position.relative
        width = LinearDimension.auto
        height = LinearDimension.auto
        padding = "2px"
        border = "2px solid #373737"
        backgroundColor = Color("#4F4F4F")

        nav {
            marginBottom = 8.px
        }
    }
}