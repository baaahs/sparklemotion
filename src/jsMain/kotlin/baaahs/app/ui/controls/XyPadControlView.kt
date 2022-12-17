package baaahs.app.ui.controls

import baaahs.app.ui.appContext
import baaahs.app.ui.gadgets.xypad.xyPad
import baaahs.control.OpenXyPadControl
import baaahs.show.live.ControlProps
import baaahs.ui.and
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.css.*
import kotlinx.css.properties.boxShadow
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.useContext
import styled.StyleSheet

private val XyPadControlView = xComponent<XyPadProps>("XyPadControl") { props ->
    val appContext = useContext(appContext)
    val controlsStyles = appContext.allStyles.controls

    val xyPad = props.xyPadControl.xyPad


    div(props.containerClasses ?: (+XyPadStyles.container and props.xyPadControl.inUseStyle)) {
        xyPad {
            attrs.xyPad = xyPad
        }

        div(+controlsStyles.feedTitle) { +xyPad.title }
    }
}

external interface XyPadProps : Props {
    var controlProps: ControlProps
    var xyPadControl: OpenXyPadControl
    var containerClasses: String?
    var backgroundClasses: String?
}

object XyPadStyles : StyleSheet("app-ui-controls-xypad", isStatic = true) {
    val container by css {
        display = Display.flex
        border = "3px inset"
    }

    val background by css {
        position = Position.relative
        background = "linear-gradient(#DDFFDD, #88EE88)"
        overscrollBehavior = OverscrollBehavior.contain
    }

    val centerLine by css {
        position = Position.absolute
        backgroundColor = Color("#222222").withAlpha(.5)
        boxShadow(rgba(0, 0, 0, .2), 0.px, 0.px, 1.px, 1.px)
        pointerEvents = PointerEvents.none
    }

    val knob by css {
        width = 20.px
        height = 20.px
        border = "2px outset"

        borderRadius = 3.px
        boxShadow(Color("#222222").withAlpha(.3), 1.px, 1.px, 1.px, 1.px)
        background = "linear-gradient(#6ABBC0, #00A4D1)"

        position = Position.absolute
        cursor = Cursor.move
    }

    val crosshairs by css {
        position = Position.absolute
        backgroundColor = Color("#44FF44")
        boxShadow(Color("#224422").withAlpha(.3), 0.px, 0.px, 1.px, 1.px)
        pointerEvents = PointerEvents.none
    }

    val resetButton by css {
        position = Position.absolute
        left = 0.px
        bottom = 0.px
    }
}

fun RBuilder.xyPadControl(handler: RHandler<XyPadProps>) =
    child(XyPadControlView, handler = handler)