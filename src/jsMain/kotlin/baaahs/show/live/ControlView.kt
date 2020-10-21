package baaahs.show.live

import baaahs.app.ui.AppContext
import baaahs.app.ui.controls.*
import react.FunctionalComponent

external interface ControlProps<T : OpenControl> : GenericControlProps {
    var control: T
}

actual interface ControlView {
    fun <P: ControlProps<in OpenControl>> getReactElement(): FunctionalComponent<P>

    fun onEdit(appContext: AppContext) = Unit
}

actual fun getViewFor(openControl: OpenControl): ControlView =
    when (openControl) {
        is OpenButtonControl -> ButtonControlView(openControl)
        is OpenButtonGroupControl -> ButtonGroupControlView(openControl)
        is OpenGadgetControl -> GadgetControlView(openControl)
        is OpenVisualizerControl -> VisualizerControlView(openControl)
        else -> UnknownControlView(openControl)
    }
