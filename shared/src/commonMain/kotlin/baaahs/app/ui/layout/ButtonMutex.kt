package baaahs.app.ui.layout

import baaahs.ui.Observable

class ButtonMutex(val parentControlId: String) : Observable() {
    var selectedControlId: String? by notifyOnChange(null)
}

interface MayHaveButtonMutex {
    val buttonMutex: ButtonMutex?
}