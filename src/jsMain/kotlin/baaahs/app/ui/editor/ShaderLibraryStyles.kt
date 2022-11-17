package baaahs.app.ui.editor

import kotlinx.css.*
import mui.material.styles.Theme
import styled.StyleSheet

class ShaderLibraryStyles(private val theme: Theme) : StyleSheet("app-ui-editor-ShaderLibrary", isStatic = true) {
    val dialogPaper by css {
        height = 90.vh
        position = Position.absolute
        bottom = 0.px
    }

    val dialogTitle by css {
        display = Display.flex
        justifyContent = JustifyContent.spaceBetween
    }

    val dialogTitleActions by css {
        padding = ".5em"
    }

    val dialogContent by css {
        display = Display.flex
        flexDirection = FlexDirection.column
        overflowY = Overflow.hidden
    }

    val results by css {
        position = Position.relative
        flex = Flex(1, 0, FlexBasis.auto)
        top = 0.px
        left = 0.px
        right = 0.px
        bottom = 0.px
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