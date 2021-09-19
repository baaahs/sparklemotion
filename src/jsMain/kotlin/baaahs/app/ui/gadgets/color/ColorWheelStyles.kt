package baaahs.app.ui.gadgets.color

import kotlinx.css.*
import kotlinx.css.properties.boxShadow
import kotlinx.css.properties.s
import kotlinx.css.properties.transition
import styled.StyleSheet

object ColorWheelStyles : StyleSheet("ui", isStatic = true) {
    val root by css {
        borderRadius = .5.em
        color = Color.black
        display = Display.inlineBlock
        position = Position.relative
        minHeight = 200.px
        minWidth = 200.px
    }

    val canvasWrapper by css {
        position = Position.relative
    }

    val canvas by css {
        top = 0.px
        left = 0.px
        width = 100.pct
        position = Position.absolute
    }

    val picker by css {
        top = 0.px
        left = 0.px
        cursor = Cursor.grab
        width = 20.px
        height = 20.px
        backgroundColor = Color.white
        borderRadius = 100.pct
        border = "2px solid white"
        transition(::borderColor, .1.s)
        transition(::color, 0.s)

        hover {
            boxShadow(Color.black.withAlpha(.15), 0.px, 1.px, 3.px)
        }

        descendants(".selected") {
            border = "2px solid black"
        }

        descendants(".grabbing") {
            cursor = Cursor.grabbing
        }
    }

    val draggablePicker by css {
        zIndex = 0
        top = (-10).px
        left = (-10).px
        position = Position.absolute

        descendants(".dragging") {
            zIndex = 10
        }
    }

    val dragging by css {}
    val grabbing by css {}
    val selected by css {}
    val active by css {}

    val harmonyModes by css {}
    val harmonyMode by css {}
}
