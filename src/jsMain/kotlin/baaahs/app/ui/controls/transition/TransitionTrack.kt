package baaahs.app.ui.controls.transition

import baaahs.ui.Observable

class TransitionTrack(
    val title: String
) : Observable() {
    var position = 0f
        set(value) {
            field = value
            notifyChanged()
        }

    val onAir get() = position > 0f

    var onScreen = false
        set(value) {
            field = value
            notifyChanged()
        }
}