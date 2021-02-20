package baaahs.sim.ui

import baaahs.app.ui.ThemeStyles
import baaahs.ui.descendants
import kotlinx.css.*
import kotlinx.css.properties.border
import styled.StyleSheet

class SimulatorStyles(
    private val appUiStyles: ThemeStyles
) : StyleSheet("sim-ui", isStatic = true) {
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

    val beatsDiv by css(subsection) {
        child("div") {
            backgroundColor = Color.lightCyan
            display = Display.inlineBlock
            width = 1.em
            padding(0.2.em)
            marginRight = 2.px
            textAlign = TextAlign.center
        }

        child("div.selected") {
            backgroundColor = Color.lightCoral
        }
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

    val glslCodeSheet by css {
        position = Position.fixed
        left = 5.em
        bottom = 5.em
        zIndex = 100
        maxHeight = 50.vh
        maxWidth = 50.em
        display = Display.flex
        flexDirection = FlexDirection.column

        hover {
            descendants(appUiStyles.dragHandle) {
                opacity = 1
            }
        }
    }

    val dragHandle by css {
        position = Position.absolute
        right = 5.px
        top = 5.px
        zIndex = 1
        cursor = Cursor.move
    }

    val glslCodePaper by css {
        padding(1.em)
        display = Display.flex
        flexDirection = FlexDirection.column
    }

    val glslCodeDiv by css {
        maxHeight = 50.vh
        maxWidth = 50.em
        overflow = Overflow.scroll
    }
}