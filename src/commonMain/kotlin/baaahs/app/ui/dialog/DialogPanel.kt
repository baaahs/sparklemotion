package baaahs.app.ui.dialog

import baaahs.Severity
import baaahs.ui.Icon
import baaahs.ui.View

interface DialogPanel {
    val title: String
    val listSubhead: String?
    val icon: Icon?
    val problemLevel: Severity? get() = null

    fun getNestedDialogPanels(): List<DialogPanel> = emptyList()
    fun getView(): View
}