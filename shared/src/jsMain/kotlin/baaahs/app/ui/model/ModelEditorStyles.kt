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
        paddingLeft = 1.em
        justifyContent = JustifyContent.start
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
        flexDirection = FlexDirection.row
        bottom = 4.px
        right = 0.px
        backgroundColor = Color(theme.palette.background.paper).withAlpha(.8)
        opacity = .9
    }

    val visualizerSnapToGrid by css {
        width = LinearDimension.minContent
        display = Display.flex

        input {
            width = 5.em
            textAlign = TextAlign.right
        }
    }

    val visualizerNumberInput by css {
        paddingTop = 0.px.important
        paddingBottom = 0.px.important
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
        overflow = Overflow.hidden
    }

    val headerEditor by css {
        display = Display.inlineFlex

        "input" {
            backgroundColor = Color(theme.palette.background.paper).withAlpha(.1)
        }
        "input:focus" {
            backgroundColor = Color(theme.palette.background.paper).withAlpha(.5)
        }
    }

    val mainPanelForEntityType by css {
        paddingLeft = 1.em
        paddingRight = 1.em
        marginBottom = 1.em
    }
    val propertiesEditSection by css {
        whiteSpace = WhiteSpace.nowrap
        paddingLeft = 0.px.important
        paddingRight = 0.px.important
        userSelect = UserSelect.none
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

    val transformEditSection by css(propertiesEditSection) {
        padding = Padding(0.em)

        input {
            width = 4.em
        }
    }

    val columns by css {
        display = important(Display.grid)
        gap = 1.em
        paddingLeft = 0.px
        paddingRight = 0.px
        paddingTop = .5.em

        "> label" {
            left = (-.9).rem.important
            fontSize = 1.15.em
            fontWeight = FontWeight.bolder
            lineHeight = 1.1.em.lh
        }
    }
    val twoColumns by css(columns) {
        gridTemplateColumns = GridTemplateColumns(1.fr, 1.fr)
    }
    val threeColumns by css(columns) {
        gridTemplateColumns = GridTemplateColumns(1.fr, 1.fr, 1.fr)
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
            whiteSpace = WhiteSpace.pre
        }
    }
}