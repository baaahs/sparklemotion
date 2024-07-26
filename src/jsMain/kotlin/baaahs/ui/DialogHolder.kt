package baaahs.ui

import react.RBuilder

actual interface DialogHolder {
    actual fun showDialog(view: View)
    actual fun showMenuDialog(title: String, options: List<DialogMenuItem>)
    actual fun closeDialog()

    fun showDialog(block: RBuilder.() -> Unit) {
        showDialog(object : JsView {
            override fun RBuilder.render() {
                block()
            }
        })
    }
}