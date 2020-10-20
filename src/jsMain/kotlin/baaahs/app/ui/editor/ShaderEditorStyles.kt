package baaahs.app.ui.editor

import baaahs.app.ui.ShaderPreviewStyles
import baaahs.ui.child
import baaahs.ui.important
import kotlinx.css.*
import materialui.styles.muitheme.MuiTheme
import materialui.styles.palette.dark
import materialui.styles.palette.paper
import styled.StyleSheet

class ShaderEditorStyles(private val theme: MuiTheme) : StyleSheet("app-ui-editor-ShaderEditor", isStatic = true) {
    val propsAndPreview by css {
        display = Display.flex
        gap = Gap(1.em.toString())
        paddingTop = 1.em
        paddingRight = 1.em
        maxHeight = previewHeight

        child(ShaderPreviewStyles.container) {
            grow(Grow.NONE)
        }
    }

    val propsTabsAndPanels by css {
        grow(Grow.GROW)
        display = Display.flex
        flexDirection = FlexDirection.row
    }

    val tabsContainer by css {
        backgroundColor = theme.palette.primary.dark
    }

    val propsPanel by css {
        display = Display.flex
        flexDirection = FlexDirection.row
        marginLeft = 1.em
        marginRight = 1.em
        overflow = Overflow.scroll

        child("TABLE") {
            display = Display.block
        }
    }

    val shaderProperties by css {
        display = Display.flex
        flexDirection = FlexDirection.column

        child("*") {
            important(::paddingBottom, 1.em)
        }
    }

    val tab by css {
        important(::backgroundColor, theme.palette.background.paper)
        important(::textAlign, TextAlign.right)
    }

    companion object {
        val previewWidth = 250.px
        val previewHeight = 250.px
    }
}