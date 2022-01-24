package baaahs.ui

expect interface View

expect interface Icon

expect interface DialogHolder {
    fun showDialog(view: View)
    fun closeDialog()
}

@Deprecated("Find something nicer.")
expect fun confirm(message: String): Boolean