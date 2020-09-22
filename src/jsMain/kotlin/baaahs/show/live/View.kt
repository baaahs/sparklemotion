package baaahs.show.live

import baaahs.app.ui.controls.*
import react.FunctionalComponent

external interface ControlProps<T : OpenControl> : GenericControlProps {
    var control: T
}

actual interface View {
    fun <P: ControlProps<in OpenControl>> getReactElement(): FunctionalComponent<P>

    fun onEdit(props: GenericControlProps) = Unit
}

actual fun getViewFor(openControl: OpenControl): View =
    when (openControl) {
        is OpenButtonControl -> ButtonView(openControl)
        is OpenButtonGroupControl -> ButtonGroupView(openControl)
        is OpenGadgetControl -> GadgetView(openControl)
        else -> UnknownView(openControl)
    }
