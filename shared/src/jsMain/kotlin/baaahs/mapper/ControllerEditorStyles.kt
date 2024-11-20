package baaahs.mapper

import baaahs.app.ui.StyleConstants
import baaahs.ui.asColor
import baaahs.ui.important
import baaahs.ui.selector
import kotlinx.css.*
import kotlinx.css.FlexDirection.column
import kotlinx.css.properties.lh
import mui.material.styles.Theme
import styled.StyleSheet

class ControllerEditorStyles(val theme: Theme) : StyleSheet("app-ui-scene-editor", isStatic = true) {
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
        td {
            whiteSpace = WhiteSpace.nowrap
        }
    }

    val navigatorPane by css {
        display = Display.flex
        flexDirection = column
        height = 100.pct
    }
    val hideNavigatorPane by css {
        display = Display.none
    }
    val screenWidthNavigatorPane by css {
        width = 100.vw
    }
    val navigatorPaneContent by css {
        minHeight = 0.px
        overflow = Overflow.scroll
    }
    val navigatorPaneActions by css {
    }
    val navigatorPaneHeader by css {
        lineHeight = 1.5.em.lh
    }

    val statusDot by css {
        width = 10.px
        height = 10.px
        backgroundColor = Color.red
        borderRadius = 50.pct
        display = Display.inlineBlock
    }

    val controllerIcon by css {
        width = 1.5.em
        marginRight = 1.em
        verticalAlign = VerticalAlign.middle
        marginLeft = (-1).em
    }

    val fixtureListItem by css {
        listStyleType = ListStyleType.circle
    }

    val defaultConfigs by css {
        backgroundColor = theme.palette.info.main.asColor()
        marginTop = 1.em
        marginBottom = 1.em
        paddingBottom = 1.em

        header {
            backgroundColor = theme.palette.info.main.asColor().darken(20)
            marginBottom = 1.em
        }

        child("div") {
            marginLeft = 1.em
            marginRight = 1.em
        }
    }

    val button by css {
        textTransform = TextTransform.none
    }

    val accordionPreview by css {
        color = theme.palette.text.primary.asColor().withAlpha(.5)
        paddingLeft = 1.em
    }

    val accordionDetails by css {
        padding = Padding(0.px)
    }

    val previewChip by css {
        display = Display.flex
        flexDirection = column
        alignItems = Align.center
        lineHeight = 90.pct.lh

        firstChild { }
        lastChild { fontSize = .9.em }
    }

    val accordionRoot by css {
        backgroundColor = theme.palette.primary.main.asColor().withAlpha(.125)
        borderTopLeftRadius = 0.px.important
        borderTopRightRadius = 0.px.important
    }

    val expansionPanelSummaryContent by css {
        overflow = Overflow.hidden
        justifyContent = JustifyContent.spaceBetween
    }

    val expansionPanelSummaryChips by css {
        overflow = Overflow.scroll
    }

    val configCardOuter by css {
        backgroundColor = theme.palette.primary.main.asColor()
            .withAlpha(.25).blend(Color(theme.palette.background.paper))
        padding = Padding(.5.em)

        adjacentSibling(".$name-configCardOuter") {
            marginTop = 1.em
        }
    }

    val configCardInner by css {
        backgroundColor = Color(theme.palette.background.paper)
        paddingLeft = 1.em
        paddingTop = .75.em
        paddingBottom = .75.em
        display = Display.flex
        flexDirection = column
        gap = 1.em
    }

    val expansionPanelDetails by css {
        flexDirection = column
    }

    val divider by css {
        height = 2.px
        margin = Margin(.5.em, 0.em)
    }

    val configEditorRow by css {
        display = Display.flex
        flexDirection = FlexDirection.row
        flexWrap = FlexWrap.wrap
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