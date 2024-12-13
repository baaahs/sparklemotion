package baaahs.mapper

import baaahs.ui.asColor
import baaahs.ui.important
import baaahs.ui.inset
import emotion.css.keyframes
import kotlinx.css.Align
import kotlinx.css.Border
import kotlinx.css.Color
import kotlinx.css.Display
import kotlinx.css.FlexDirection
import kotlinx.css.FlexDirection.column
import kotlinx.css.FlexWrap
import kotlinx.css.GridAutoRows
import kotlinx.css.GridTemplateColumns
import kotlinx.css.GridTemplateRows
import kotlinx.css.JustifyContent
import kotlinx.css.ListStyleType
import kotlinx.css.Margin
import kotlinx.css.Overflow
import kotlinx.css.Padding
import kotlinx.css.TextTransform
import kotlinx.css.UserSelect
import kotlinx.css.VerticalAlign
import kotlinx.css.WhiteSpace
import kotlinx.css.alignItems
import kotlinx.css.backgroundColor
import kotlinx.css.border
import kotlinx.css.borderRadius
import kotlinx.css.borderTopLeftRadius
import kotlinx.css.borderTopRightRadius
import kotlinx.css.color
import kotlinx.css.columnGap
import kotlinx.css.display
import kotlinx.css.em
import kotlinx.css.flexDirection
import kotlinx.css.flexWrap
import kotlinx.css.fontSize
import kotlinx.css.gap
import kotlinx.css.gridTemplateColumns
import kotlinx.css.gridTemplateRows
import kotlinx.css.header
import kotlinx.css.height
import kotlinx.css.input
import kotlinx.css.justifyContent
import kotlinx.css.lineHeight
import kotlinx.css.listStyleType
import kotlinx.css.margin
import kotlinx.css.marginBottom
import kotlinx.css.marginLeft
import kotlinx.css.marginRight
import kotlinx.css.marginTop
import kotlinx.css.minHeight
import kotlinx.css.opacity
import kotlinx.css.overflow
import kotlinx.css.overflowY
import kotlinx.css.padding
import kotlinx.css.paddingBottom
import kotlinx.css.paddingLeft
import kotlinx.css.paddingTop
import kotlinx.css.pct
import kotlinx.css.properties.IterationCount
import kotlinx.css.properties.lh
import kotlinx.css.properties.s
import kotlinx.css.px
import kotlinx.css.td
import kotlinx.css.textTransform
import kotlinx.css.userSelect
import kotlinx.css.verticalAlign
import kotlinx.css.vw
import kotlinx.css.whiteSpace
import kotlinx.css.width
import mui.material.styles.Theme
import styled.StyleSheet
import styled.animation

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
        userSelect = UserSelect.none
        marginTop = 2.px
        lineHeight = 1.5.em.lh
        backgroundColor = theme.palette.primary.dark.asColor()
            .darken(20).desaturate(20)
    }

    val scanningIndicator by css {
        display = Display.inline
        animation(duration = 7.s, iterationCount = IterationCount.infinite) {
            keyframes {
                0 { opacity = 1 }
                20 { opacity = 1 }
                25 { opacity = 0 }
                95 { opacity = 0 }
                100 { opacity = 1 }
            }
        }
    }

    val statusDot by css {
        width = 10.px
        height = 10.px
        backgroundColor = Color.grey
        border = Border(1.px, inset, Color.lightGrey)
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
        paddingLeft = 1.em
        justifyContent = JustifyContent.start
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

    val accordionSummaryContent by css {
//        overflow = Overflow.hidden
        justifyContent = JustifyContent.spaceBetween
    }
    val accordionSummaryContentRows by css {
        flexDirection = FlexDirection.column
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