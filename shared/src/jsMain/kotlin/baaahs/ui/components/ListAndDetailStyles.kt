package baaahs.ui.components

import baaahs.app.ui.StyleConstants
import baaahs.ui.asColor
import kotlinx.css.*
import kotlinx.css.Display
import kotlinx.css.FlexDirection
import kotlinx.css.flexDirection
import kotlinx.css.height
import kotlinx.css.pct
import kotlinx.css.properties.TextDecoration
import mui.material.styles.Theme
import styled.StyleSheet

class ListAndDetailStyles(val theme: Theme) : StyleSheet("app-ui-scene-editor", isStatic = true) {
    val listSheet by css {
        display = Display.flex
        flexDirection = FlexDirection.column
        width = 100.vw
        height = 100.pct
    }

    val detailSheet by css {
        position = Position.absolute
        display = Display.flex
        flexDirection = FlexDirection.column
        width = 100.vw
        height = 100.pct
        zIndex = StyleConstants.Layers.aboveStickyTableHeaders
    }

    val detailHeader by css {
        a {
            color = theme.palette.primary.contrastText.asColor().withAlpha(0.5)
            paddingRight = 2.em
            textDecoration = TextDecoration.none
        }
    }
}