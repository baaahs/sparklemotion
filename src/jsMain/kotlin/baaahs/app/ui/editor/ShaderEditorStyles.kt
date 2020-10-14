package baaahs.app.ui.editor

import baaahs.app.ui.ShaderPreviewStyles
import baaahs.ui.child
import baaahs.ui.important
import kotlinx.css.*
import materialui.styles.muitheme.MuiTheme
import materialui.styles.palette.paper
import styled.StyleSheet

class ShaderEditorStyles(private val theme: MuiTheme) : StyleSheet("app-ui-editor-ShaderEditor", isStatic = true) {
    val propsAndPreview by css {
        display = Display.flex
        gap = Gap(1.em.toString())
        paddingTop = 1.em
        paddingRight = 1.em

        child(ShaderPreviewStyles.container) {
            grow(Grow.NONE)
        }
    }

    val propsPanels by css {
        grow(Grow.GROW)
        display = Display.flex
        flexDirection = FlexDirection.row
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
}