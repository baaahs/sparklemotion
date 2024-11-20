package baaahs.ui.components

import baaahs.app.ui.StyleConstants
import baaahs.ui.asColor
import kotlinx.css.*
import kotlinx.css.properties.TextDecoration
import mui.material.styles.Theme
import styled.StyleSheet

class ListAndDetailStyles(val theme: Theme) : StyleSheet("app-ui-scene-editor", isStatic = true) {
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