package baaahs.ui.components

import baaahs.app.ui.StyleConstants
import baaahs.ui.*
import kotlinx.css.*
import kotlinx.css.properties.Timing
import kotlinx.css.properties.s
import mui.material.styles.Theme
import styled.StyleSheet

class UiComponentStyles(val theme: Theme) : StyleSheet("app-ui-components", isStatic = true) {
    val paletteFrame by css {
        display = Display.flex
        flexDirection = FlexDirection.column
        position = Position.fixed
        zIndex = StyleConstants.Layers.floatingWindows
        border = Border(1.px, outset, theme.paperHighContrast)
        transition(::opacity, duration = 1.s)

        hover {
            descendants(selector(::paletteActions)) {
                opacity = 1
            }
        }
    }

    val paletteActions by css {
        opacity = .2
        transition(::opacity, duration = 0.25.s, timing = Timing.linear)
        position = Position.absolute
        right = 2.px
        top = 0.5.em
        display = Display.flex
        flexDirection = FlexDirection.row
        zIndex = 101
    }

    val dragHandle by css {
//        cursor = Cursor.grab
    }

    val closeBox by css {
    }

    val resizeHandle by css {
        opacity = .2
        transition(::visibility, duration = 0.25.s, timing = Timing.linear)
        position = Position.absolute
        right = 2.px
        top = 0.5.em
        zIndex = 101
    }

    val paper by css {
        flexGrow = 1.0
        display = Display.flex
        flexDirection = FlexDirection.column
        overflow = Overflow.hidden
    }

    val codeContainer by css {
        position = Position.relative
//        color = Color(theme.palette.context.primary.toString()).withAlpha(.9)
        backgroundColor = Color(theme.paperContrast(.15).toString())
        padding = Padding(0.5.em)
        border = Border(2.px, baaahs.ui.inset, theme.palette.info.main.asColor())
    }

    val code by css {
        whiteSpace = WhiteSpace.preWrap
        userSelect = UserSelect.all
        margin = Margin(0.px)

        // Line numbers:
        before {
            declarations["counterReset"] = "listing"
        }

        "code" {
            display = Display.block
            declarations["counterIncrement"] = "listing"
            whiteSpace = WhiteSpace.pre
            userSelect = UserSelect.all

            before {
                declarations["content"] = "counter(listing) \"  \""
                display = Display.inlineBlock
                width = 4.em
                paddingLeft = LinearDimension.auto
                marginLeft = LinearDimension.auto
                textAlign = TextAlign.right
            }

            nthChild("even") {
                backgroundColor = Color(theme.paperContrast(.2).toString())
            }
        }
    }

    val dagSvg by css {
        flexGrow = 1.0
        backgroundColor = Color.whiteSmoke
        height = 100.vh // So we occupy some space in a Flex layout.
    }
}