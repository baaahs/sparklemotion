package baaahs.ui

import react.RBuilder

actual interface DialogHolder {
    actual fun showDialog(view: View)
    actual fun closeDialog()

    fun showDialog(block: RBuilder.() -> Unit) {
        showDialog(object : View {
            override fun RBuilder.render() {
                block()
            }
        })
    }
}