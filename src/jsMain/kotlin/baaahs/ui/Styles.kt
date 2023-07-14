package baaahs.ui

import kotlinx.css.*
import kotlinx.css.properties.scale
import kotlinx.css.properties.transform
import styled.StyleSheet

object Styles : StyleSheet("ui", isStatic = true) {
    val toolbar by css {
        display = Display.flex
        alignItems = Align.center
        justifyContent = JustifyContent.spaceBetween
    }
    
    val showName by css {
        flex = Flex(1.0, 1.0)
        padding = Padding(0.px, 8.px)
        display = Display.flex
    }
    val showNameInput by css {
        display = Display.flex
        background = "none"
        border = Border.none
        padding = Padding(0.px, 8.px)
        color = Color.white
        flex = Flex(1.0, 1.0)

        focus {
            outline = Outline.none
        }
    }

    val previewBar by css {
        display = Display.flex
        alignItems = Align.center
        justifyContent = JustifyContent.spaceBetween
    }

    val preview by css {}
    val status by css {
        whiteSpace = WhiteSpace.pre
        height = 180.px
        overflow = Overflow.scroll
    }
    val controls by css {
        transform { scale(.5) }
        height = 180.px

        children("div") {
            display = Display.flex
        }
    }

    val buttons by css {
        display = Display.flex
    }

    val iconButton by css {
        color = Color.white
        padding = Padding(8.px)

        hover {
            cursor = Cursor.pointer
            color = Color("#d7d7d7")
        }
    }

    val textEditor by css {
        width = 100.pct
        height = 100.pct
    }

    val fileDialogFileList by css {
        height = 50.vh
        overflowY = Overflow.scroll
        border = Border(1.px, groove)
    }

    val helpInline by css {
        display = Display.inline
        padding = Padding(0.em, .5.em)
    }

    val guruMeditationErrorContainer by css {
        backgroundColor = Color.black
        color = Color.red
        margin = Margin(0.px)
        padding = Padding(.5.em)
    }

    val guruMeditationErrorBox by css {
        margin = Margin(1.em)
        padding = Padding(1.em)
        display = Display.flex
        flexDirection = FlexDirection.row
        alignItems = Align.center
        justifyContent = JustifyContent.spaceEvenly

        pre {
            whiteSpace = WhiteSpace.preWrap
        }

        button {
            border = Border(1.px, BorderStyle.solid, Color.red)
            color = Color.red
        }
    }

    val guruMeditationErrorIcon by css {
        float = Float.left
        paddingRight = 2.em
    }

    val guruMeditationErrorStackTrace by css {
        marginTop = 2.em
        marginBottom = 2.em
        paddingLeft = 2.em
        paddingRight = 2.em
        maxHeight = 70.vh
        display = Display.flex
        flexDirection = FlexDirection.column
    }
}