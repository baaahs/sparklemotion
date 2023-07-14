package baaahs.app.ui.patchmod

import kotlinx.css.*
import mui.material.styles.Theme
import styled.StyleSheet

class PatchModStyles(private val theme: Theme) : StyleSheet("app-ui-patchmod", isStatic = true) {
    val container by css {
        display = Display.flex
        flexDirection = FlexDirection.row
    }

    val lightboxShaderPreviewContainer by css {
        position = Position.relative
        width = 300.px
        height = 300.px
        cursor = Cursor.grab
        flex(0.0, 0.0, FlexBasis.auto)
    }

    val controls by css {
        display = Display.flex
        flexDirection = FlexDirection.row
        flex(Flex.GROW)
        maxWidth = 300.px
        overflowX = Overflow.scroll
        padding = "1em"
    }

    val xyPadContainer by css {
        top = 0.px
        left = 0.px
        width = 100.pct
        height = 100.pct
        position = Position.absolute
    }

    val xyPadBackground by css {

    }

    val sliderContainer by css {
        top = 0.px
        right = 0.px
        width = 55.px
        height = 100.pct
        position = Position.absolute
        backgroundColor = Color.black.withAlpha(.5)
    }
}