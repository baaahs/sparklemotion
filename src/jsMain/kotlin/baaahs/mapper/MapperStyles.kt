package baaahs.mapper

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
    }
    
    val controls by css {
        position = Position.absolute
        zIndex = 1
    }
    
    val mapping2dCanvas by css {
        position = Position.absolute
        display = Display.block
        top = 0.px
        bottom = 0.px
        left = 0.px
        right = 0.px
        margin = "auto"
    }
    
    val mapping3dContainer by css {
        canvas {
            position = Position.absolute
            display = Display.block
            top = 0.px
            bottom = 0.px
            left = 0.px
            right = 0.px
            margin = "auto"
            put("mix-blend-mode", "lighten")
        }
    }

    val snapshotDiv by css {
        position = Position.absolute
        top = 0.pct + 10.px
        right = 10.px
        border = "1px groove white"
    }

    val baseDiv by css {
        position = Position.absolute
        top = 25.pct + 20.px
        right = 10.px
        border = "1px groove white"
    }

    val diffDiv by css {
        position = Position.absolute
        top = 50.pct + 30.px
        right = 10.px
        border = "1px groove white"
    }

    val panelMaskDiv by css {
        position = Position.absolute
        top = 75.pct + 40.px
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
}