package baaahs.app.ui.editor.layout

import baaahs.ui.asColor
import baaahs.ui.important
import kotlinx.css.*
import kotlinx.css.properties.LineHeight
import mui.material.styles.Theme
import styled.StyleSheet

class LayoutEditorStyles(theme: Theme) : StyleSheet("app-ui-editor-LayoutEditorStyles", isStatic = true) {
    val dialog by css {
        width = 80.vw
        height = 60.vh
    }

    val outerContainer by css {
        display = Display.grid
        gridTemplateColumns = GridTemplateColumns("min-content auto auto")
        gap = 1.em
    }

    val listSubheader by css {
        important(::lineHeight, LineHeight.normal)
    }

    val deleteTabIcon by css {
        color = Color("#aa0000")
        position = Position.absolute
        right = 0.px
        padding = Padding(3.px)

        hover {
            color = Color("#dd0000")
        }
    }

    val editorGrid by css {
        marginTop = 1.em

        children {
            borderRight = Border(1.px, BorderStyle.solid, theme.palette.primary.dark.asColor())
            borderBottom = Border(1.px, BorderStyle.solid, theme.palette.primary.dark.asColor())
            padding = Padding(2.px)
        }
    }

    val gridSizeEditor by css {
        color = theme.palette.primary.contrastText.asColor()
        backgroundColor = theme.palette.primary.main.asColor()

        input {
            color = theme.palette.primary.contrastText.asColor()
            width = 4.em
            textAlign = TextAlign.right
        }
    }

//    val gridSizeEditorTextField by css {
//        paddingTop = 5.px
//    }

    val gridAreaEditor by css {
    }

    val gridAreaEdge by css {
        border = Border.none
    }

    val gridSizeEditorMenuAffordance by css {
        display = Display.inlineBlock
        padding = Padding(2.px, 2.px, 5.px, 2.px)
        height = 100.pct
        verticalAlign = VerticalAlign.middle

        child("span") {
            position = Position.relative
            top = 6.px
        }
    }
}