package baaahs.ui.slider

import baaahs.ui.Observable

class Handle(
    val id: String,
    var value: Double,
    /** Called when the user releases the handle. */
    val onChange: (Double) -> Unit = {},
    /** Called when the user moves the handle, potentially very frequently. */
    val onUpdate: (Double) -> Unit = onChange
) : Observable() {
    fun update(value: Double) {
        if (this.value == value) return
        this.value = value
        notifyChanged()
    }

    fun changeTo(value: Double, isFinal: Boolean) {
        if (this.value != value || isFinal) {
            this.value = value
            this.notifyChanged()
            if (isFinal) this.onChange(value) else this.onUpdate(value)
        }
    }
}