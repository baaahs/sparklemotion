package baaahs.app.ui.model

import baaahs.ui.important
import kotlinx.css.*
import kotlinx.css.properties.LineHeight
import kotlinx.css.properties.lh
import mui.material.styles.Theme
import styled.StyleSheet
import web.cssom.important

class ModelEditorStyles(val theme: Theme) : StyleSheet("app-ui-model-editor", isStatic = true) {
    val editorPanesPortrait by css {
        display = Display.grid
        gridTemplateRows = GridTemplateRows(
            GridAutoRows.minMax(40.pct, 40.pct),
            GridAutoRows.minMax(60.pct, 60.pct)
        )
        width = 100.pct
        height = 100.pct
    }

    val editorPanesLandscape by css {
        display = Display.grid
        gridTemplateColumns = GridTemplateColumns(
            GridAutoRows.auto,
            GridAutoRows.minMax(LinearDimension.auto, 23.em)
        )
        width = 100.pct
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

    val newEntityButton by css {
        paddingLeft = 2.em
    }

    val visualizerPane by css {
        position = Position.relative
        height = 100.pct
    }

    val visualizer by css {
        position = Position.absolute
        top = 0.px
        left = 0.px
        bottom = 0.px
        right = 0.px
    }

    val visualizerToolbar by css {
        position = Position.absolute
        display = Display.flex
        flexDirection = FlexDirection.column
        top = 10.px
        left = 10.px
        backgroundColor = Color(theme.palette.background.paper).withAlpha(.8)
    }

    val domOverlay by css {
        position = Position.absolute
        top = 0.px
        left = 0.px
        bottom = 0.px
        right = 0.px
        pointerEvents = PointerEvents.none
    }

    val domOverlayItem by css {
        opacity = .7
        pointerEvents = important(PointerEvents.none)
    }

    val domOverlayItemInnerDiv by css {
        whiteSpace = WhiteSpace.nowrap
        backfaceVisibility = Visibility.hidden
    }

    val propertiesPane by css {
        height = 100.pct
        overflow = Overflow.scroll
    }

    val propertiesEditSection by css {
        whiteSpace = WhiteSpace.nowrap
//        color = theme.palette.primary.contrastText
//        backgroundColor = theme.palette.primary.main

        input {
//            color = theme.palette.primary.contrastText
            fontSize = .9.em
        }

        header {
            color = Color.inherit
            backgroundColor = Color.inherit
            fontSize = LinearDimension.inherit
            fontWeight = FontWeight.inherit
            lineHeight = 1.5.em.lh
            padding = Padding(.5.em, 0.px, 0.px, 0.px)
        }
    }

    val gridSizeInput by css(propertiesEditSection) {
        width = 5.em
        textAlign = TextAlign.right
    }

    val transformEditSection by css(propertiesEditSection) {
        paddingTop = 0.em
        paddingBottom = 0.em
        paddingLeft = 1.em
        paddingRight = 1.em

        input {
            width = 4.em
            textAlign = TextAlign.right
        }
    }

    val transformThreeColumns by css {
        display = important(Display.grid)
        gridTemplateColumns = GridTemplateColumns(1.fr, 1.fr, 1.fr)
        gap = 1.em

        "> label" {
            left = (-.9).rem.important
            fontSize = 1.15.em
            fontWeight = FontWeight.bolder
            lineHeight = 1.1.em.lh
        }
    }

    val partialUnderline by css {
        before {
            right = LinearDimension.inherit
            width = 100.pct - 1.5.em
        }
    }

    val jsonEditorTextField by css {
        textarea {
            fontSize = .8.em
            fontFamily = "monospace"
            lineHeight = LineHeight("1.1")
            whiteSpace = WhiteSpace.nowrap
        }
    }
}