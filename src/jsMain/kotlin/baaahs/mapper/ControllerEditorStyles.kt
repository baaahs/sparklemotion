package baaahs.mapper

import kotlinx.css.*
import materialui.styles.muitheme.MuiTheme
import materialui.styles.palette.dark
import materialui.styles.palette.main
import styled.StyleSheet

class ControllerEditorStyles(val theme: MuiTheme) : StyleSheet("app-ui-scene-editor", isStatic = true) {
    val editorPanes by css {
        display = Display.grid
        gridTemplateColumns = GridTemplateColumns(
            GridAutoRows.minMax(25.em, 25.pct),
            GridAutoRows.auto,
            GridAutoRows.minMax(15.em, 15.pct)
        )
        columnGap = 1.em
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
        gridTemplateRows = GridTemplateRows.minContent
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

    val button by css {
        textTransform = TextTransform.none
    }

    val expansionPanelRoot by css {
        backgroundColor = theme.palette.primary.main
    }

    val configCardOuter by css {
        backgroundColor = theme.palette.primary.main.lighten(5)
        padding(.5.em)

        adjacentSibling(".$name-configCardOuter") {
            marginTop = 1.em
        }
    }

    val configCardInner by css {
        backgroundColor = theme.palette.primary.dark
        paddingLeft = 1.em
        paddingTop = .75.em
        paddingBottom = .75.em
        display = Display.flex
        flexDirection = FlexDirection.column
        gap = 1.em
    }

    val expansionPanelDetails by css {
        flexDirection = FlexDirection.column
    }

    val divider by css {
        height = 2.px
        margin(.5.em, 0.em)
    }

    val configEditorRow by css {
        display = Display.flex
        flexDirection = FlexDirection.row
        gap = 1.em
    }

    val pixelArrayConfigEditorRow by css(configEditorRow) {
        input {
            width = 7.em
        }
    }

    val dmxTransportConfigEditorRow by css(configEditorRow) {
        input {
            width = 7.em
        }
    }
}