package baaahs.show.live

import baaahs.app.ui.controls.ButtonGroupView
import baaahs.app.ui.controls.ButtonView
import baaahs.app.ui.controls.GadgetView

actual fun getViewFor(openControl: OpenControl): View =
    when (openControl) {
        is OpenButtonControl -> ButtonView(openControl)
        is OpenButtonGroupControl -> ButtonGroupView(openControl)
        is OpenGadgetControl -> GadgetView(openControl)
        else -> UnknownView(openControl)
    }

class UnknownView(val openControl: OpenControl) : View
