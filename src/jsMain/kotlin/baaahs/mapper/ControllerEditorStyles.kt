package baaahs.mapper

import kotlinx.css.*
import materialui.styles.muitheme.MuiTheme
import materialui.styles.palette.dark
import styled.StyleSheet

class ControllerEditorStyles(val theme: MuiTheme) : StyleSheet("app-ui-scene-editor", isStatic = true) {
    val editorPanes by css {
        display = Display.grid
        gridTemplateColumns = GridTemplateColumns(
            GridAutoRows.minMax(25.em, 25.pct),
            GridAutoRows.auto,
            GridAutoRows.minMax(15.em, 15.pct)
        )
        gridTemplateRows = GridTemplateRows(100.pct)
        height = 100.pct
    }

    val controllersTable by css {
        display = Display.block
        overflowY = Overflow.scroll
        height = 100.pct
    }

    val navigatorPane by css {
        display = Display.grid
        gridTemplateRows = GridTemplateRows(GridAutoRows.minContent, GridAutoRows.auto)
        height = 100.pct
    }
    val navigatorPaneContent by css {
        minHeight = 0.px
        overflow = Overflow.scroll
    }
    val navigatorPaneActions by css {
        display = Display.grid
        gridTemplateColumns = GridTemplateColumns(
            GridAutoRows.minContent,
            GridAutoRows.auto
        )
        gridTemplateRows = GridTemplateRows(100.pct)
    }

    val fixturesPane by css {
        position = Position.relative
        height = 100.pct
    }

    val propertiesPane by css {
        display = Display.grid
        gridTemplateRows = GridTemplateRows(GridAutoRows.minContent, GridAutoRows.auto)
        height = 100.pct
    }

    val propertiesPaneContent by css {
        minHeight = 0.px
        overflow = Overflow.scroll
    }

    val searchBarPaper by css {
        backgroundColor = theme.palette.primary.dark.darken(20)
        marginTop = 8.px
        marginBottom = 8.px
        marginLeft = 1.em
    }
}