package baaahs.app.ui.gadgets.slider

import baaahs.app.ui.linearRepeating
import baaahs.ui.*
import kotlinx.css.*
import kotlinx.css.properties.*
import mui.material.styles.Theme
import styled.StyleSheet

class ThemedStyles(val theme: Theme) : StyleSheet("app-ui-gadgets-Slider", isStatic = true) {
    private val indicatorColor = Color("#00FF28")

    val slider by css {
        position = Position.relative
        height = 100.pct
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
        put("textShadow", "0px 1px 1px ${theme.paperHighContrast}")
        border = Border(2.px, baaahs.ui.groove, theme.paperLowContrast)
    }

    val label by css {
        position = Position.absolute
        width = 100.pct
        flex = Flex(0.0, 0.0, FlexBasis.auto)
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

        img {
            width = 20.px
            height = 40.px
            position = Position.relative
            top = 2.px
            left = 11.px

            filter = "drop-shadow(1px 1px 1px rgba(0, 0, 0, .3))"
            pointerEvents = PointerEvents.none
        }
    }

    val handleWrapper by css {
        position = Position.absolute
        transform.translate((-50).pct, (-50).pct)
        zIndex = 2
        width = 20.px
        height = 40.px
        borderRadius = 2.px
        boxShadow += BoxShadow(rgba(0, 0, 0, 0.3), 1.px, 1.px, 1.px, 1.px)
        background = "linear-gradient(#6ABBC0, #00A4D1)"
        display = Display.flex
        flexDirection = FlexDirection.column
        justifyContent = JustifyContent.spaceBetween
        alignItems = Align.center
    }

    val handleNormal by css {
        width = 20.px
        height = 40.px
        position = Position.relative
        top = 2.px
        left = 11.px

        filter = "drop-shadow(1px 1px 1px rgba(0, 0, 0, .3))"
    }

    val handleMin by css {
        width = 20.px
        height = 40.px
        position = Position.relative
        top = 2.px
        left = 11.px

        filter = "drop-shadow(1px 1px 1px rgba(0, 0, 0, .3))"
    }
    val handleMax by css {
        width = 20.px
        height = 40.px
        position = Position.relative
        top = 2.px
        left = 11.px

        filter = "drop-shadow(1px 1px 1px rgba(0, 0, 0, .3))"
    }

    val handleNotch by css {
        height = 1.px
        backgroundColor = Color.black
        borderBottom = Border(1.px, BorderStyle.solid, Color("#B8D5CF"))
        width = 100.pct
    }

    val handleNotchMiddle by css {
        height = 2.px
        padding = Padding(2.px)
        border = Border(1.px, baaahs.ui.inset, Color("#00A4D1"))
        backgroundColor = indicatorColor
        width = 50.pct
    }

    val handleNotchLower by css {
        borderBottom = Border.none
        borderTop = Border(1.px, BorderStyle.solid, Color("#B8D5CF"))
    }

    val altHandleWrapper by css {
        position = Position.absolute
        zIndex = 10

        descendants("path") {
            put("fill", "darkorange")
        }
    }

    val altHandleLeft by css {
        position = Position.absolute
        width = 30.px
        height = 20.px
        transform.translateY((-50).pct)
        paddingLeft = 10.px
        top = 5.px
        right = (-1).px
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
        width = 7.px
        top = (-3).px
        bottom = (-3).px
        transform.translateX((-50).pct)
        borderRadius = 7.px
        pointerEvents = PointerEvents.none
        backgroundColor = theme.paperLowContrast
        boxShadow += BoxShadowInset(rgba(0, 0, 0, .85), 1.px, 1.px, 1.px, 0.px)
        boxShadow += BoxShadowInset(rgba(255, 255, 255, 0.2), (-1).px, (-1).px, 1.px, 0.px)
    }

    val tickMark by css {
        position = Position.absolute
        marginTop = (-20).px
        marginLeft = 10.px
        height = 1.px
        width = 6.px
        backgroundColor = theme.paperMediumContrast
    }

    val defaultTickMark by css(tickMark) {
        backgroundColor = Color.orange
    }

    val tickText by css {
        position = Position.absolute
//        right = (-30).px
        marginTop = (-5).px
        marginLeft = 20.px
        fontSize = 10.px
        color = theme.paperMediumContrast
        transform.translateY(50.pct)
        pointerEvents = PointerEvents.none
        userSelect = UserSelect.none
    }

    val track by css {
        position = Position.absolute
        zIndex = 1
        backgroundColor = indicatorColor
        backgroundImage = Image(linearRepeating(indicatorColor, indicatorColor.darken(40), 2.px, 0.deg))
        borderRadius = 7.px
        cursor = Cursor.pointer
        width = 2.px
        transform.translateX((-50).pct)
        transition(::top, duration = 0.05.s)
        transition(::bottom, duration = 0.05.s)
    }
}