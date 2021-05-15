package baaahs.app.ui.editor.layout

import baaahs.ui.important
import kotlinx.css.*
import kotlinx.css.properties.LineHeight
import kotlinx.css.properties.borderBottom
import kotlinx.css.properties.borderRight
import materialui.styles.muitheme.MuiTheme
import materialui.styles.palette.contrastText
import materialui.styles.palette.dark
import materialui.styles.palette.main
import styled.StyleSheet

class LayoutEditorStyles(theme: MuiTheme) : StyleSheet("app-ui-editor-LayoutEditorStyles", isStatic = true) {
    val outerContainer by css {
        display = Display.grid
        gridTemplateColumns = GridTemplateColumns("auto auto auto")
        gap = Gap(1.em.toString())
    }

    val listSubheader by css {
        important(::lineHeight, LineHeight.normal)
    }

    val editorGrid by css {
        marginTop = 1.em

        children {
            borderRight(1.px, BorderStyle.solid, theme.palette.primary.dark)
            borderBottom(1.px, BorderStyle.solid, theme.palette.primary.dark)
            padding(2.px)
        }
    }

    val gridSizeEditor by css {
        color = theme.palette.primary.contrastText
        backgroundColor = theme.palette.primary.main

        input {
            color = theme.palette.primary.contrastText
            width = 4.em
            textAlign = TextAlign.right
        }
    }

    val gridAreaEditor by css {
    }

    val gridAreaEdge by css {
        border = "none"
    }

    val gridSizeMenuAffordance by css {
        display = Display.inlineBlock
//        border(2.px, BorderStyle.solid, theme.palette.primary.light)
//        borderRadius = 5.px
//        position = Position.fixed
//        transform { scale(.5) }
        padding(2.px)
        height = 100.pct
        verticalAlign = VerticalAlign.middle
//        backgroundColor = theme.palette.background.paper
//        boxShadow = theme.shadows[3]
    }
}