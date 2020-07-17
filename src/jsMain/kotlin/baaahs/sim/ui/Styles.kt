package baaahs.ui

import kotlinx.css.*
import kotlinx.css.properties.border
import styled.StyleSheet

object SimulatorStyles : StyleSheet("SimulatorView", isStatic = true) {
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

    val showsDiv by css(subsection) {
        child("span") {
            display = Display.inlineBlock
            padding(0.2.em)
            marginRight = 2.px
            marginTop = 2.px
        }

        child("span.selected") {
            borderColor = Color.black
        }
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

    val dataWithUnit by Styles.css {
        textAlign = TextAlign.right
    }
}