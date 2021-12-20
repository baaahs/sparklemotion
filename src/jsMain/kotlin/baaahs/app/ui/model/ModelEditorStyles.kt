package baaahs.app.ui.model

import kotlinx.css.*
import materialui.styles.muitheme.MuiTheme
import styled.StyleSheet

class ModelEditorStyles(val theme: MuiTheme) : StyleSheet("app-model", isStatic = true) {
    val editorPanes by css {
        display = Display.grid
        gridTemplateColumns = GridTemplateColumns(
            GridAutoRows(20.pct),
            GridAutoRows(60.pct),
            GridAutoRows(20.pct)
        )
        height = 100.pct
    }

    val navigatorPane by css {}

    val visualizerPane by css {
        position = Position.relative
    }

    val visualizer by css {
        position = Position.absolute
        top = 0.px
        left = 0.px
        bottom = 0.px
        right = 0.px
    }

    val propertiesPane by css {}

    val transformEditBox by css {
        whiteSpace = WhiteSpace.nowrap
//        color = theme.palette.primary.contrastText
//        backgroundColor = theme.palette.primary.main

        input {
//            color = theme.palette.primary.contrastText
            width = 5.em
            textAlign = TextAlign.right
        }
    }
}