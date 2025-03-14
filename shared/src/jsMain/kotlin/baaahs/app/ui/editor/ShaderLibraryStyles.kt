package baaahs.app.ui.editor

import baaahs.ui.asColor
import baaahs.ui.isSmallScreen
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
    }
    val dialogTitleDiv by css {
        display = Display.flex
        justifyContent = JustifyContent.spaceBetween
        alignItems = Align.center
        color = theme.palette.primary.light.asColor()
    }

    val searchBox by css {
        flex = Flex.GROW
    }

    val dialogTitleActions by css {
        padding = Padding(.5.em)
    }

    val noPadding by css {
        padding = Padding(.0.em)
    }

    val dialogContent by css {
        display = Display.flex
        flexDirection = FlexDirection.column
        overflowY = Overflow.hidden

        theme.isSmallScreen {
            padding = Padding(0.px, 0.px)
        }
    }

    val dialogHeader by css {
        display = Display.flex
        alignItems = Align.center

        theme.isSmallScreen {
            flexDirection = FlexDirection.column
        }
    }

    val resultsSizeIcons by css {
        width = 16.px
        height = 16.px
    }

    val filterMenu by css {
        paddingTop = 0.em
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

        theme.isSmallScreen {
            gap = .25.em
        }
    }
}