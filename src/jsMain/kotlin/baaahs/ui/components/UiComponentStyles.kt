package baaahs.ui.components

import baaahs.app.ui.StyleConstants
import baaahs.ui.paperContrast
import baaahs.ui.paperHighContrast
import baaahs.ui.selector
import kotlinx.css.*
import kotlinx.css.properties.Timing
import kotlinx.css.properties.s
import kotlinx.css.properties.transition
import mui.material.styles.Theme
import styled.StyleSheet

class UiComponentStyles(val theme: Theme) : StyleSheet("app-ui-components", isStatic = true) {
    val paletteFrame by css {
        display = Display.flex
        flexDirection = FlexDirection.column
        position = Position.fixed
        zIndex = StyleConstants.Layers.floatingWindows
        border = "1px outset ${theme.paperHighContrast}"
        transition(::opacity, duration = 1.s)

        hover {
            descendants(selector(::dragHandle), selector(::resizeHandle)) {
                opacity = 1
            }
        }
    }

    val dragHandle by css {
        opacity = .2
        transition(::visibility, duration = 0.25.s, timing = Timing.linear)
        position = Position.absolute
        right = 2.px
        top = 0.5.em
        zIndex = 101
//        cursor = Cursor.grab
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
        display = Display.flex
        flexDirection = FlexDirection.column
        overflow = Overflow.hidden
    }

    val codeContainer by css {
        position = Position.relative
//        color = Color(theme.palette.context.primary.toString()).withAlpha(.9)
        backgroundColor = Color(theme.paperContrast(.15).toString())
        padding = 0.5.em.value
        border = "2px inset ${theme.palette.info.main}"
    }

    val code by css {
        whiteSpace = WhiteSpace.preWrap
        userSelect = UserSelect.all

        // Line numbers:
        before {
            declarations["counterReset"] = "listing"
        }

        code {
            display = Display.block
            declarations["counterIncrement"] = "listing"

            before {
                declarations["content"] = "counter(listing) \". \""
                display = Display.inlineBlock
                width = 2.em
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
        backgroundColor = Color.whiteSmoke
    }
}