package baaahs.app.ui.editor

import kotlinx.css.*
import mui.material.styles.Theme
import styled.StyleSheet

class ShaderLibraryStyles(private val theme: Theme) : StyleSheet("app-ui-editor-ShaderLibrary", isStatic = true) {
    val dialogContent by css {
        display = Display.flex
        flexDirection = FlexDirection.column
    }

    val results by css {
        position = Position.relative
        flex(1, 0, FlexBasis.auto)
        height = 100.vh
    }

    val shaderGridScrollContainer by css {
        position = Position.absolute
        top = 0.px
        left = 0.px
        right = 0.px
        bottom = 0.px
        overflowY = Overflow.scroll
    }

    val shaderGrid by css {
        display = Display.grid
        gap = 1.em
    }
}