package baaahs.app.ui.editor.layout

import baaahs.ui.asColor
import baaahs.ui.important
import kotlinx.css.*
import kotlinx.css.properties.LineHeight
import kotlinx.css.properties.borderBottom
import kotlinx.css.properties.borderRight
import mui.material.styles.Theme
import styled.StyleSheet

class LayoutEditorStyles(theme: Theme) : StyleSheet("app-ui-editor-LayoutEditorStyles", isStatic = true) {
    val outerContainer by css {
        display = Display.grid
        gridTemplateColumns = GridTemplateColumns("auto auto auto")
        gap = 1.em
    }

    val listSubheader by css {
        important(::lineHeight, LineHeight.normal)
    }

    val editorGrid by css {
        marginTop = 1.em

        children {
            borderRight(1.px, BorderStyle.solid, theme.palette.primary.dark.asColor())
            borderBottom(1.px, BorderStyle.solid, theme.palette.primary.dark.asColor())
            padding(2.px)
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
        border = "none"
    }

    val gridSizeEditorMenuAffordance by css {
        display = Display.inlineBlock
        padding(2.px, 2.px, 5.px, 2.px)
        height = 100.pct
        verticalAlign = VerticalAlign.middle

        child("span") {
            position = Position.relative
            top = 6.px
        }
    }
}