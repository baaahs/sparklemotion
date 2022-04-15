package baaahs.client.document

import baaahs.ui.Observable
import baaahs.util.globalLaunch

class EditMode(initialMode: Mode) : Observable() {
    private var mode = initialMode

    val isOn: Boolean get() = mode.isOn
    val isOff: Boolean get() = !mode.isOn
    val isAvailable: Boolean get() = mode != Mode.Never

    fun toggle() {
        when (mode) {
            Mode.Never, Mode.Off -> turnOn()
            Mode.On -> turnOff()
        }
    }

    fun turnOn() {
        if (mode == Mode.Never) {
            mode = Mode.Off
            notifyChanged()

            globalLaunch {
                mode = Mode.On
                notifyChanged()
            }
        } else {
            mode = Mode.On
            notifyChanged()
        }
    }

    fun turnOff() {
        mode = Mode.Off
        notifyChanged()
    }

    enum class Mode(val isOn: Boolean) {
        Never(false),
        Off(false),
        On(true)
    }
}
