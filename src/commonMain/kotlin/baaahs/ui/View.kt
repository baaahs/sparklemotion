package baaahs.ui

interface View

expect interface Icon

expect interface DialogHolder {
    fun showDialog(view: View)
    fun showMenuDialog(title: String, options: List<DialogMenuItem>)
    fun closeDialog()
}

sealed interface DialogMenuItem {
    object Divider : DialogMenuItem

    data class Header(
        val title: String
    ) : DialogMenuItem

    data class Option(
        val title: String,
        val onSelect: () -> Unit
    ) : DialogMenuItem
}

@Deprecated("Find something nicer.")
expect fun confirm(message: String): Boolean