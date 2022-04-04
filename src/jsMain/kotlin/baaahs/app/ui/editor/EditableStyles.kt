package baaahs.app.ui.editor

import baaahs.ui.StuffThatShouldComeFromTheTheme
import baaahs.ui.child
import baaahs.ui.important
import kotlinx.css.*
import mui.material.styles.Theme
import mui.system.Breakpoint
import styled.StyleSheet

class ThemedEditableStyles(val theme: Theme) : StyleSheet("app-ui-editor-Editable-themed", isStatic = true) {
    val drawer by css {
        minHeight = 85.vh

        theme.breakpoints.up(Breakpoint.md)() {
            margin(horizontal = 5.em)
            important(::maxHeight, 85.vh)
        }
    }

    val dialogContent by css {
        padding(0.px)
        display = Display.flex
        flexDirection = FlexDirection.column
    }

    val expandSwitchLabel by css {
        grow(Grow.GROW)
        paddingLeft = 1.em
    }

    val singlePanel by css {
        grow(Grow.GROW)
        display = Display.flex
        flexDirection = FlexDirection.column

        ".ui-shaderEditor" {
            grow(Grow.GROW)

            ".ui-textEditor" {
                height = LinearDimension.initial
            }
        }
    }
}

object EditableStyles : StyleSheet("app-ui-editor-Editable", isStatic = true) {
    val cardWidth = 175.px


    val tabsList by css {
        important(::paddingLeft, 1.em)
    }

    val patchOverview by css {
        position = Position.relative
        backgroundColor = StuffThatShouldComeFromTheTheme.lightBackgroundColor
        display = Display.grid
        gridTemplateColumns = GridTemplateColumns.repeat("auto-fit, minmax(175px, 1fr)")
        gap = 1.em
        padding(1.em)
    }

    val shaderCard by css {
        maxWidth = cardWidth
    }

    val shaderCardContent by css {
    }
    val shaderCardActions by css {
        child(this@EditableStyles, ::shaderCardContent) {
            flex(1.0)
        }

        child("button") {
            flex(0.0)
            padding(0.px)
        }
    }

    val propertiesSection by css {
        paddingTop = .5.em
        paddingBottom = 1.em
    }
}