package baaahs.app.ui.patchmod

import baaahs.Gadget
import baaahs.app.ui.appContext
import baaahs.app.ui.controls.sliderControl
import baaahs.app.ui.gadgets.xypad.xyPad
import baaahs.gadgets.XyPad
import baaahs.geom.Vector2F
import baaahs.show.live.OpenPatch
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import baaahs.util.useResizeListener
import org.w3c.dom.HTMLElement
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.useContext

private val PositionAndScaleView = xComponent<PositionAndScaleProps>("PositionAndScale") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.patchModStyles

    val xyPad = props.patchMod.positionXyPad

    val containerRef = ref<HTMLElement>()
    var padSize by state<Vector2F?> { null }
    useResizeListener(containerRef) { width, height ->
        padSize = Vector2F(width.toFloat(), height.toFloat())
    }
    onMount(xyPad) {
        val listener = { gadget: Gadget ->
            console.log("xypad: ${(gadget as XyPad).position}")
        }
        xyPad.listen(listener)
        withCleanup { xyPad.unlisten(listener) }
    }

    div(+styles.xyPadContainer) {
        ref = containerRef

        xyPad {
            attrs.xyPad = xyPad
            attrs.backgroundClasses = +styles.xyPadBackground
            attrs.padSize = padSize
        }
    }

    div(+styles.sliderContainer) {
        sliderControl {
            attrs.slider = props.patchMod.scaleSlider
        }
    }
}

external interface PositionAndScaleProps : Props {
    var patchMod: PositionAndScalePatchMod
    var patch: OpenPatch
}

fun RBuilder.positionAndScale(handler: RHandler<PositionAndScaleProps>) =
    child(PositionAndScaleView, handler = handler)