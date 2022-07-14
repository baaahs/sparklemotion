package baaahs.app.ui.patchmod

import kotlinx.css.*
import mui.material.styles.Theme
import styled.StyleSheet

class PatchModStyles(private val theme: Theme) : StyleSheet("app-ui-patchmod", isStatic = true) {
    val lightboxShaderPreviewContainer by css {
        position = Position.relative
        width = 300.px
        height = 300.px
        cursor = Cursor.grab
    }

    val xyPadContainer by css {
        top = 0.px
        left = 0.px
        position = Position.absolute
    }

    val xyPadBackground by css {

    }
}