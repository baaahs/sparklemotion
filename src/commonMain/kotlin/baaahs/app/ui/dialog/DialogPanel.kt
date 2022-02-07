package baaahs.app.ui.dialog

import baaahs.sm.webapi.Severity
import baaahs.ui.Icon
import baaahs.ui.View

interface DialogPanel {
    val title: String
    val listSubhead: String? get() = null
    val icon: Icon? get() = null
    val problemLevel: Severity? get() = null

    fun getNestedDialogPanels(): List<DialogPanel> = emptyList()
    fun getView(): View
}