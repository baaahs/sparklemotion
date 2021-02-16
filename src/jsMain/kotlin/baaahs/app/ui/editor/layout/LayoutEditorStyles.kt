package baaahs.app.ui.editor.layout

import kotlinx.css.*
import kotlinx.css.properties.borderBottom
import kotlinx.css.properties.borderRight
import materialui.styles.muitheme.MuiTheme
import materialui.styles.palette.dark
import styled.StyleSheet

class LayoutEditorStyles(theme: MuiTheme) : StyleSheet("app-ui-editor-LayoutEditorStyles", isStatic = true) {
    val editorGrid by css {
        children {
            borderRight(1.px, BorderStyle.solid, theme.palette.primary.dark)
            borderBottom(1.px, BorderStyle.solid, theme.palette.primary.dark)
            padding(2.px)
        }
    }

    val gridSizeEditor by css {
        input {
            width = 4.em
        }
    }

    val gridAreaEditor by css {
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