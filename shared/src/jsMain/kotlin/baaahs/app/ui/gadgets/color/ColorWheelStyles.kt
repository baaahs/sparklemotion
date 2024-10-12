package baaahs.app.ui.gadgets.color

import baaahs.ui.name
import baaahs.ui.rgba
import baaahs.ui.selector
import baaahs.ui.transition
import kotlinx.css.*
import kotlinx.css.properties.BoxShadow
import kotlinx.css.properties.s
import styled.StyleSheet
import baaahs.app.ui.controls.Styles as ControlStyles

object ColorWheelStyles : StyleSheet("app-ui-gadgets-color", isStatic = true) {
    val root by css {
        borderRadius = .5.em
        color = Color.black
        display = Display.inlineBlock
        position = Position.relative
        width = 100.pct
        height = 100.pct
    }

    val dragging by css {}
    val grabbing by css {}
    val selected by css {}
    val active by css {}

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
        backgroundColor = Color.transparent
        borderRadius = 100.pct
        border = Border(2.px, BorderStyle.solid, Color.black)
        transition(::borderColor, .1.s)
        transition(::color, 0.s)

        hover {
            boxShadow += BoxShadow(Color.black.withAlpha(.15), 0.px, 1.px, 3.px)
        }

        descendants(".selected") {
            border = Border(2.px, BorderStyle.solid, Color.black)
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

    val harmonyModes by css {
        display = Display.flex
        flex = Flex(1.0, 1.0, FlexBasis.auto)
        marginTop = 1.em
        justifyContent = JustifyContent.spaceBetween
    }

    val harmonyMode by css {
        textTransform = TextTransform.uppercase
        fontSize = 12.px
        color = Color.white
        backgroundColor = rgba(32, 32, 32, 0.91)
        border = Border.none
        display = Display.flex
        flex = Flex(1.0, 1.0, FlexBasis.auto)
        cursor = Cursor.pointer
        padding = Padding(8.px)
        outline = Outline.none

        descendants(".active") {
            backgroundColor = Color("#4BBDC2")
        }

        descendants("+ &") {
            borderLeft = Border(1.px, BorderStyle.solid, rgba(13, 13, 13, 0.91))

        }
    }

    val global = CssBuilder().apply {
        ".${ControlStyles.notExplicitlySized.name}" {
            descendants(selector(::root)) {
                minWidth = 150.px
                minHeight = 150.px
            }
        }
    }
}
