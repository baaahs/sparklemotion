package baaahs.ui

expect interface View

expect interface Icon

expect interface DialogHolder {
    fun showDialog(view: View)
    fun showMenuDialog(title: String, options: List<DialogMenuOption>)
    fun closeDialog()
}

interface DialogMenuOption {
    val title: String
    val onSelect: () -> Unit

    object Divider : DialogMenuOption {
        override val title: String get() = TODO("not implemented")
        override val onSelect: () -> Unit get() = TODO("not implemented")
    }

    data class Option(
        override val title: String,
        override val onSelect: () -> Unit
    ) : DialogMenuOption
}

@Deprecated("Find something nicer.")
expect fun confirm(message: String): Boolean