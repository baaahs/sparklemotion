package baaahs.mapper

import baaahs.ui.asColor
import baaahs.ui.important
import kotlinx.css.*
import kotlinx.css.properties.border
import kotlinx.css.properties.scaleX
import kotlinx.css.properties.transform
import mui.material.styles.Theme
import styled.StyleSheet

class MapperStyles(val theme: Theme) : StyleSheet("mapper", isStatic = true) {
    val green = Color("#00ff00")
    
    val screen by css {
        width = 100.pct
        height = 100.pct
        position = Position.relative
    }
    
    val controls by css {
        display = Display.flex
        flexDirection = FlexDirection.column
        height = 100.pct
        margin(0.em, 1.em)

        button {
            minWidth = 0.px
            padding = "5px"
            margin = "2px"
            backgroundColor = theme.palette.primary.main.asColor().withAlpha(.2)
        }
    }

    val controlsRow by css {
        display = Display.flex
        flexDirection = FlexDirection.row
        width = 100.pct
        justifyContent = JustifyContent.spaceBetween
        alignItems = Align.center
        margin(1.em, 0.em)

        div {
            display = Display.flex
            flexDirection = FlexDirection.column
        }
    }

    val fillParent by css {
        position = Position.absolute
        top = 0.px
        bottom = 0.px
        left = 0.px
        right = 0.px
    }

    val mapping2dCanvas by css(fillParent) {
        margin = "auto"
    }
    
    val mapping3dContainer by css(fillParent) {
        canvas {
            margin = "auto"
            put("mix-blend-mode", "lighten")
        }
    }

    val savedImage by css(fillParent) {
        display = Display.none
    }

    val snapshotDiv by css {
        position = Position.absolute
        top = 0.pct + 5.px
        right = 10.px
        border = "1px groove white"
    }

    val baseDiv by css {
        position = Position.absolute
        top = 25.pct + 10.px
        right = 10.px
        border = "1px groove white"
    }

    val diffDiv by css {
        position = Position.absolute
        top = 50.pct + 15.px
        right = 10.px
        border = "1px groove white"
    }

    val panelMaskDiv by css {
        position = Position.absolute
        top = 75.pct + 20.px
        right = 10.px
        border = "1px groove white"
    }

    private val statusText by css {
        fontFamily = "'Press Start 2P', sans-serif"
        color = green
        put("textShadow", "1px 1px 3px black, -1px -1px 3px black")
    }

    val thumbnailTitle by css(statusText) {
        position = Position.absolute
        bottom = .5.em
        left = 1.em
        fontSize = 8.pt
    }

    val stats by css(statusText) {
        position = Position.absolute
        right = 20.px
        padding = "20px"
        fontSize = 8.pt
        textAlign = TextAlign.right
        width = 100.pct
    }
    
    val perfStats by css(statusText) {
        position = Position.absolute
        bottom = 10.em
        left = 1.em
        padding = "1.em"
        fontSize = 8.pt
        transform { scaleX(.75) }
        whiteSpace = WhiteSpace.pre
        border(1.px, BorderStyle.solid, green, 2.px)
    }
    
    val message by css(statusText) {
        position = Position.absolute
        bottom = 20.px
        padding = "20px"
        fontSize = 16.pt
        textAlign = TextAlign.center
        width = 100.pct
    }
    
    val message2 by css(statusText) {
        position = Position.absolute
        bottom = 0.px
        padding = "20px"
        fontSize = 7.pt
        textAlign = TextAlign.center
        width = 100.pct
    }
    
    val table by css {
        position = Position.absolute
        top = 30.px
        padding = "20px"
        fontSize = 7.pt
        width = 400.px
        background = "rgba(0, 0, 0, .5)"
        height = 250.px
    }

    val sessionInfo by css {
        position = Position.absolute
        top = 30.px
        padding = "20px"
        fontSize = 7.pt
        overflow = Overflow.scroll
        background = "rgba(0, 0, 0, .5)"
    }

    val pixels by css {
        child("div") {
            display = Display.inlineBlock
            width = 8.px
            height = 8.px
            margin(1.px)
            border = "1px solid transparent"
        }
    }

    val skippedPixel by css {
        backgroundColor = Color.grey
    }

    val unmappedPixel by css {
        backgroundColor = Color.red
    }

    val mappedPixel by css {
        backgroundColor = Color.green
    }

    val selectedPixel by css {
        important(::border, "1px solid yellow")
    }
}