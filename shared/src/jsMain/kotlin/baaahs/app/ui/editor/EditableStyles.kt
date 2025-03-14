package baaahs.app.ui.editor

import baaahs.app.ui.controls.Styles
import baaahs.ui.StuffThatShouldComeFromTheTheme
import baaahs.ui.child
import baaahs.ui.important
import baaahs.ui.isSmallScreen
import kotlinx.css.*
import mui.material.styles.Theme
import mui.system.Breakpoint
import styled.StyleSheet

class ThemedEditableStyles(val theme: Theme) : StyleSheet("app-ui-editor-Editable-themed", isStatic = true) {
    val drawer by css {
        minHeight = 85.vh
        theme.isSmallScreen {
            minHeight = 100.vh
        }

        theme.breakpoints.up(Breakpoint.md)() {
            margin = Margin(horizontal = 5.em)
            maxHeight = 85.vh.important
        }
    }

    val dialogContent by css {
        padding = Padding(0.px)
        display = Display.flex
        flexDirection = FlexDirection.column
    }

    val expandSwitchLabel by css {
        flex = Flex.GROW
        paddingLeft = 1.em
    }

    val singlePanel by css {
        flex = Flex.GROW
        display = Display.flex
        flexDirection = FlexDirection.column
        padding = Padding(24.px, 16.px)

        ".ui-shaderEditor" {
            flex = Flex.GROW

            ".ui-textEditor" {
                height = LinearDimension.initial
            }
        }
    }

    val singlePanelNoMargin by css {
        padding = Padding(0.px)
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
        padding = Padding(1.em)
    }

    val shaderCard by css {
        maxWidth = cardWidth
    }

    val shaderCardContent by css {
    }
    val shaderCardActions by css {
        child(this@EditableStyles, ::shaderCardContent) {
            flex = Flex(1.0)
        }

        child("button") {
            flex = Flex(0.0)
            padding = Padding(0.px)
        }
    }

    val propertiesSection by css {
        paddingTop = .5.em
        paddingBottom = 1.em
    }

    val absFill by css {
        position = Position.absolute
        top = 0.px
        left = 0.px
        bottom = 0.px
        right = 0.px
    }

    val shaderCardDenseText by css(Styles.buttonControlWithPreview, absFill) {
        zIndex = 1000
        textAlign = TextAlign.center
        height = LinearDimension.fitContent
        marginTop = LinearDimension.auto
        marginBottom = LinearDimension.auto
    }
}