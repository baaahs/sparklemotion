package baaahs.app.ui.patchmod

import baaahs.Gadget
import baaahs.GadgetListener
import baaahs.app.ui.appContext
import baaahs.app.ui.gadgets.xypad.xyPad
import baaahs.gadgets.XyPad
import baaahs.show.live.OpenPatch
import baaahs.ui.*
import react.*
import react.dom.div

private val PositionAndScaleView = xComponent<PositionAndScaleProps>("PositionAndScale") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.patchModStyles

    val xyPad = props.patchMod.positionXyPad

//    onMount(xyPad) {
//        val listener = { gadget: Gadget ->
//
//        }
//        xyPad.listen(listener)
//        withCleanup { xyPad.unlisten(listener) }
//    }

    div(+styles.xyPadContainer) {
        xyPad {
            attrs.xyPad = xyPad
            attrs.backgroundClasses = +styles.xyPadBackground
        }
    }
}

external interface PositionAndScaleProps : Props {
    var patchMod: PositionAndScalePatchMod
    var patch: OpenPatch
}

fun RBuilder.positionAndScale(handler: RHandler<PositionAndScaleProps>) =
    child(PositionAndScaleView, handler = handler)