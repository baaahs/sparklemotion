package baaahs.mapper

import baaahs.ui.asColor
import baaahs.ui.important
import baaahs.ui.inset
import emotion.css.keyframes
import kotlinx.css.*
import kotlinx.css.FlexDirection.column
import kotlinx.css.properties.IterationCount
import kotlinx.css.properties.lh
import kotlinx.css.properties.s
import mui.material.styles.Theme
import mui.system.Breakpoint.Companion.sm
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
        declarations["padding"] = "0".important
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

    val accordionSummaryRoot by css {
        paddingRight = .5.em.important

        // Hide fixture list when accordion is open.
        ".Mui-expanded" {
            ".app-ui-scene-editor-accordionPreview" {
                opacity = 0
                fontSize = .5.px
            }
        }
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

    val newEntityDialogRoot by css {
        theme.breakpoints.down(sm)() {
            margin = Margin(0.em)
        }
    }
    val newEntityDialogPaper by css {
        theme.breakpoints.down(sm)() {
            width = LinearDimension("calc(100% - 2em)")
            margin = Margin(0.em)
        }
    }
    val newEntityDialogContent by css {
        theme.breakpoints.down(sm)() {
            paddingLeft = 1.em
            paddingRight = 1.em
        }
    }
}