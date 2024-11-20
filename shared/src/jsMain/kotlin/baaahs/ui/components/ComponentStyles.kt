package baaahs.ui.components

import baaahs.app.ui.StyleConstants
import baaahs.ui.asColor
import baaahs.ui.important
import kotlinx.css.*
import kotlinx.css.properties.TextDecoration
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
    val listSheetSmall by css {
        position = Position.relative
        display = Display.flex
        flexDirection = FlexDirection.column
        width = 100.vw
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
        gridTemplateColumns = GridTemplateColumns("2fr 3fr")
        gap = 1.em
        height = 100.pct
    }

    val listLarge by css {
        position = Position.relative
        display = Display.flex
        flexDirection = FlexDirection.column
    }

    val detailLarge by css {
        display = Display.flex
        flexDirection = FlexDirection.column
    }

    val detailContent by css {
        overflowY = Overflow.scroll
    }

    val detailHeader by css {
        a {
            color = theme.palette.primary.contrastText.asColor().withAlpha(0.5)
            paddingRight = 2.em
            textDecoration = TextDecoration.none
        }
    }
}