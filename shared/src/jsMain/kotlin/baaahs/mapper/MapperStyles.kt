package baaahs.mapper

import baaahs.ui.asColor
import baaahs.ui.groove
import baaahs.ui.important
import kotlinx.css.*
import kotlinx.css.properties.lh
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
        margin = Margin(0.em, 1.em)
        overflow = Overflow.scroll

        button {
            minWidth = 0.px
            padding = Padding(5.px)
            margin = Margin(2.px)
            backgroundColor = theme.palette.primary.main.asColor().withAlpha(.2)
        }
    }

    val threeDControls by css {
        position = Position.absolute
        left = 20.px
        bottom = 20.px
        padding = Padding(5.px)
        fontSize = 8.pt
        backgroundColor = Color(theme.palette.background.paper).withAlpha(10.0)
    }

    val controlsRow by css {
        display = Display.flex
        flexDirection = FlexDirection.row
        width = 100.pct
        justifyContent = JustifyContent.spaceBetween
        alignItems = Align.center
        margin = Margin(1.em, 0.em)

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
        margin = Margin(LinearDimension.auto)
    }
    
    val mapping3dContainer by css(fillParent) {
        display = Display.flex

        canvas {
            margin = Margin(LinearDimension.auto)
            put("mix-blend-mode", "lighten")
        }
    }

    val savedImage by css(fillParent) {
        position = Position.absolute
        display = Display.none
    }

    val snapshotDiv by css {
        position = Position.absolute
        top = 0.pct + 5.px
        right = 10.px
        border = Border(1.px, groove, Color.white)
    }

    val baseDiv by css {
        position = Position.absolute
        top = 25.pct + 10.px
        right = 10.px
        border = Border(1.px, groove, Color.white)
    }

    val diffDiv by css {
        position = Position.absolute
        top = 50.pct + 15.px
        right = 10.px
        border = Border(1.px, groove, Color.white)
    }

    val panelMaskDiv by css {
        position = Position.absolute
        top = 75.pct + 20.px
        right = 10.px
        border = Border(1.px, groove, Color.white)
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
        padding = Padding(5.px)
        fontSize = 8.pt
        textAlign = TextAlign.right
        width = 100.pct
    }

    val perfStats by css(statusText) {
        position = Position.absolute
        bottom = 10.em
        left = 1.em
        padding = Padding(1.em)
        fontSize = 8.pt
        transform { scaleX(.75) }
        whiteSpace = WhiteSpace.pre
        border = Border(1.px, BorderStyle.solid, green)
        borderRadius = 2.px
    }

    val twoLogNMasksPalette by css(statusText) {
        position = Position.absolute
        padding = Padding(5.px, 2.em)
        backgroundColor = Color(theme.palette.background.paper).withAlpha(10.0)
    }

    val twoLogNMasks by css(statusText) {
        fontSize = .6.rem

        table {
            borderCollapse = BorderCollapse.collapse
            border = Border(1.px, BorderStyle.solid, Color.green)
        }

        th {
            border = Border(1.px, BorderStyle.solid, Color.green)
        }
        td {
            whiteSpace = WhiteSpace.pre
            border = Border(1.px, BorderStyle.solid, Color.green)
        }
    }

    val message by css(statusText) {
        position = Position.absolute
        bottom = 20.px
        padding = Padding(20.px)
        fontSize = 16.pt
        textAlign = TextAlign.center
        width = 100.pct
    }
    
    val message2 by css(statusText) {
        position = Position.absolute
        bottom = 0.px
        padding = Padding(20.px)
        fontSize = 7.pt
        textAlign = TextAlign.center
        width = 100.pct
    }
    
    val table by css {
        position = Position.absolute
        top = 30.px
        padding = Padding(20.px)
        fontSize = 7.pt
        width = 400.px
        background = "rgba(0, 0, 0, .5)"
        height = 250.px
    }

    val sessionInfo by css {
    }

    val pixels by css {
        cursor = Cursor.default
        lineHeight = 0.px.lh

        child("div") {
            display = Display.inlineBlock
            width = 8.px
            height = 8.px
            margin = Margin(1.px)
            border = Border(1.px, BorderStyle.solid, Color.transparent)
        }
    }

    val skippedPixel by css {
        backgroundColor = Color.grey
    }

    val unmappedPixel by css {
        backgroundColor = Color.red
    }

    val individuallyMappedPixel by css {
        backgroundColor = Color.orange
    }

    val twoLogNMappedPixel by css {
        backgroundColor = Color.green
    }

    val selectedPixel by css {
        important(::border, "1px solid yellow")
    }
}