package baaahs.ui.components

import baaahs.app.ui.StyleConstants
import baaahs.ui.asColor
import baaahs.ui.important
import baaahs.ui.transition
import kotlinx.css.*
import kotlinx.css.properties.TextDecoration
import kotlinx.css.properties.ms
import mui.material.styles.Theme
import styled.StyleSheet

class CollapsibleSearchBoxStyles(val theme: Theme) : StyleSheet("ui-components-collapsible-search-box", isStatic = true) {
    val searchBoxFormControl by css {
        position = Position.absolute
        right = 1.em
        top = 4.px
        flexDirection = FlexDirection.rowReverse
        width = 15.em
        fieldset {
            borderColor = Color.transparent.important
        }
    }
}

class ListAndDetailStyles(val theme: Theme) : StyleSheet("ui-components-list-and-detail", isStatic = true) {
    val transitionSpeedMs = 300

    val listSheetSmall by css {
        position = Position.relative
        display = Display.flex
        flexDirection = FlexDirection.column
        width = 100.pct
        height = 100.pct
    }

    val detailSheetSmall by css {
        position = Position.absolute
        display = Display.flex
        flexDirection = FlexDirection.column
        top = 0.px
        left = 0.px
        bottom = 0.px
        right = 0.px
        zIndex = StyleConstants.Layers.aboveStickyTableHeaders
    }

    val containerLarge by css {
        display = Display.grid
        gap = 1.em
        height = 100.pct
    }

    val containerXStacked by css {
        gridTemplateColumns = GridTemplateColumns("1fr 1fr")
        gridTemplateRows = GridTemplateRows(100.pct)
        height = 100.pct
    }

    val containerYStacked by css {
        display = Display.flex
        flexDirection = FlexDirection.column
        width = 100.pct
    }

    val listLarge by css {
        position = Position.relative
        display = Display.flex
        flexDirection = FlexDirection.column
        overflow = Overflow.auto
        minHeight = 10.em
    }

    val listLargeYStacked by css {
        flex = Flex(1)
        overflow = Overflow.hidden
    }

    val detailLarge by css {
        display = Display.flex
        flexDirection = FlexDirection.column
        height = 80.pct
        transition(::height, transitionSpeedMs.ms)
    }

    val detailLargeXStacked by css {
        height = 100.pct
    }

    val detailYStackedNoSelection by css {
        height = 0.pct
    }

    val detailContent by css {
        display = Display.flex
        justifyContent = JustifyContent.center
        overflow = Overflow.auto
    }

    val detailHeader by css {
        a {
            color = theme.palette.primary.contrastText.asColor().withAlpha(0.5)
            paddingRight = 2.em
            textDecoration = TextDecoration.none
        }
    }
}