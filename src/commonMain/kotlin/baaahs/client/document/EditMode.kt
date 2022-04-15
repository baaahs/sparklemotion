package baaahs.client.document

import baaahs.ui.Observable
import baaahs.util.globalLaunch

class EditMode(initialMode: Mode) : Observable() {
    private var mode = initialMode

    val isOn: Boolean get() = mode.isOn
    val isOff: Boolean get() = !mode.isOn
    val isTransitional: Boolean get() = mode == Mode.Transitional

    fun toggle() {
        when (mode) {
            Mode.Never -> turnOn()
            Mode.Off -> turnOn()
            Mode.Transitional -> {}
            Mode.On -> turnOff()
        }
    }

    fun turnOn() {
        mode = Mode.Transitional
        notifyChanged()

        globalLaunch {
            mode = Mode.On
            notifyChanged()
        }
    }

    fun turnOff() {
        mode = Mode.Transitional
        notifyChanged()

        globalLaunch {
            mode = Mode.Off
            notifyChanged()
        }
    }

    enum class Mode(val isOn: Boolean) {
        Never(false),
        Off(false),
        Transitional(false),
        On(true)
    }
}
