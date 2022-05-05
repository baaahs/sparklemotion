package baaahs.app.ui.gadgets.slider

import baaahs.ui.asColor
import kotlinx.css.*
import kotlinx.css.properties.*
import mui.material.styles.Theme
import styled.StyleSheet

class ThemedStyles(val theme: Theme) : StyleSheet("app-ui-gadgets-Slider", isStatic = true) {
    private val indicatorColor = Color("#00FF28")

    val slider by css {
        position = Position.relative
        width = 100.pct
        height = 100.pct
//        height = 200.px
        marginLeft = 45.pct
        marginTop = 15.px
        marginBottom = 15.px
        put("touchAction", "none")
    }

    val wrapper by css {
        position = Position.absolute
        width = 100.pct
        height = 100.pct
        display = Display.flex
        flexDirection = FlexDirection.column
        alignItems = Align.flexStart
        fontSize = 14.px
        marginBottom = 8.px
        put("textShadow", "0px 1px 1px black")
        border = "2px groove #555"
    }

    val label by css {
        position = Position.absolute
        width = 100.pct
        flex(0.0, 0.0, FlexBasis.auto)
        textAlign = TextAlign.center
        marginBottom = 20.px
    }

    val handles by css {}

    val tracks by css {}

    val ticks by css {}

    val handleTouchArea by css {
        position = Position.absolute
        transform.translate((-50).pct, (-50).pct)
        backgroundColor = Color.transparent
        zIndex = 5
        width = 42.px
        height = 45.px
        cursor = Cursor.pointer
    }

    val handleWrapper by css {
        position = Position.absolute
        transform.translate((-50).pct, (-50).pct)
        zIndex = 2
        width = 20.px
        height = 40.px
        borderRadius = 2.px
        boxShadow(rgba(0, 0, 0, 0.3), 1.px, 1.px, 1.px, 1.px)
        background = "linear-gradient(#6ABBC0, #00A4D1)"
        display = Display.flex
        flexDirection = FlexDirection.column
        justifyContent = JustifyContent.spaceBetween
        alignItems = Align.center
    }

    val handleNotch by css {
        height = 1.px
        backgroundColor = Color.black
        borderBottom = "1px solid #B8D5CF"
        width = 100.pct
    }

    val handleNotchMiddle by css {
        height = 2.px
        padding = "2px"
        border = "1px inset #00A4D1"
        backgroundColor = indicatorColor
        width = 50.pct
    }

    val handleNotchLower by css {
        borderBottom = "none"
        borderTop = "1px solid #B8D5CF"
    }

    val altHandleWrapper by css {
        position = Position.absolute
        zIndex = 2

        descendants("path") {
            put("fill", "darkorange")
        }
    }

    val altHandleLeft by css {
        position = Position.absolute
        width = 15.px
        height = 15.px
        transform.translateY((-50).pct)
        right = (-1).px
    }

    val altHandleRight by css {
        position = Position.absolute
        width = 15.px
        height = 15.px
        transform.translateY((-50).pct)
        left = 4.px
    }

    val railBackground by css {
        position = Position.absolute
        height = 100.pct
        width = 42.px
        transform.translateX((-50).pct)
        borderRadius = 7.px
        cursor = Cursor.pointer
    }

    val railChannel by css {
        position = Position.absolute
        height = 100.pct
        width = 7.px
        transform.translateX((-50).pct)
        borderRadius = 7.px
        pointerEvents = PointerEvents.none
        backgroundColor = theme.palette.text.primary.asColor()
            .withAlpha(.25)
            .blend(Color(theme.palette.background.paper))
        boxShadowInset(rgba(0, 0, 0, .85), 1.px, 1.px, 1.px, 0.px)
        boxShadowInset(rgba(255, 255, 255, 0.2), (-1).px, (-1).px, 1.px, 0.px)
    }

    val tickMark by css {
        position = Position.absolute
        marginTop = (-20).px
        marginLeft = 10.px
        height = 1.px
        width = 6.px
        backgroundColor = rgb(200, 200, 200)
    }

    val tickText by css {
        position = Position.absolute
//        right = (-30).px
        marginTop = (-5).px
        marginLeft = 20.px
        fontSize = 10.px
        color = Color("#aeaeae")
        transform.translateY(50.pct)
        pointerEvents = PointerEvents.none
        userSelect = UserSelect.none
    }

    val track by css {
        position = Position.absolute
        zIndex = 1
        backgroundColor = indicatorColor
        borderRadius = 7.px
        cursor = Cursor.pointer
        width = 2.px
        transform.translateX((-50).pct)
    }
}