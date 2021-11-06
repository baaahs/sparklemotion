package baaahs.app.ui.editor

import baaahs.app.ui.EditIntent
import baaahs.app.ui.MutableEditable
import baaahs.app.ui.dialog.DialogPanel
import baaahs.gl.Toolchain
import baaahs.show.Show
import baaahs.show.mutable.MutableShow
import baaahs.ui.Facade
import baaahs.util.Logger
import baaahs.util.UndoStack
import kotlin.math.max

class EditableManager(
    private val onApply: (Show) -> Unit
) : Facade() {
    internal val undoStack = UndoStack<ShowAndEditIntent>()
    private var appliedShow: Show? = null
    internal var session: Session? = null

    fun isEditing(): Boolean = session != null

    fun isModified(): Boolean {
        return session?.isChanged() ?: false
    }

    val currentMutableShow: MutableShow
        get() = session!!.mutableShow

    val uiTitle: String
        get() = session?.uiTitle ?: ""

    val dialogPanels: List<DialogPanel>
        get() = session?.getEditorPanels() ?: emptyList()

    private fun flatEditorPanels(): List<DialogPanel> {
        val list = arrayListOf<DialogPanel>()
        fun add(dialogPanel: DialogPanel) {
            list.add(dialogPanel)
            dialogPanel.getNestedDialogPanels().forEach { add(it) }
        }
        dialogPanels.forEach { add(it) }
        return list
    }

    private var selectedPanelIndex: Int = 0

    val selectedPanel: DialogPanel?
        get() {
            val flatList = flatEditorPanels()
            if (selectedPanelIndex >= flatList.size) return flatList.lastOrNull()
            return flatList[selectedPanelIndex]
        }

    fun openEditor(baseShow: Show, editIntent: EditIntent, toolchain: Toolchain) {
        if (isEditing()) error("already editing ${session!!.editIntent}")

        appliedShow = baseShow
        session = Session(baseShow, editIntent, toolchain)
        undoStack.reset(ShowAndEditIntent(baseShow, editIntent))
        selectedPanelIndex = 0
        notifyChanged()
    }

    fun openPanel(dialogPanel: DialogPanel) {
        val index = flatEditorPanels().indexOf(dialogPanel)
        if (index == -1) {
            logger.warn { "Unknown panel $dialogPanel" }
        }
        selectedPanelIndex = max(0, index)
        notifyChanged()
    }

    /** [DialogPanel]s should call this when they've made a change to the [MutableEditable]. */
    fun onChange(pushToUndoStack: Boolean = true) {
        session!!.onChange(pushToUndoStack)
        notifyChanged()
    }

    fun undo() = switchBaseShow(undoStack.undo())
    fun canUndo() = undoStack.canUndo()

    fun redo() = switchBaseShow(undoStack.redo())
    fun canRedo() = undoStack.canRedo()

    fun applyChanges() {
        val session = session!!
        val newShow = session.mutableShow.getShow()
        val nextEditIntent = session.editIntent.nextEditIntent()
        appliedShow = newShow
        onApply(newShow)
        switchBaseShow(ShowAndEditIntent(newShow, nextEditIntent))
    }

    private fun switchBaseShow(newShowAndEditIntent: ShowAndEditIntent) {
        session = Session(newShowAndEditIntent.show, newShowAndEditIntent.editIntent, session!!.toolchain)
        notifyChanged()
    }

    fun close() {
        undoStack.reset(null)
        appliedShow = null
        session = null
        notifyChanged()
    }

//    val handlePatchHolderApply = useCallback {
//        patchHolderEditContext?.let {
//            val mutableShow = it.mutableShow
//            webClient.onShowEdit(mutableShow)
//        }
//        patchHolderEditContext = null
//    }
//
//    val handlePatchHolderClose = useCallback {
//        patchHolderEditContext = null
//    }

    internal inner class Session(
        baseShow: Show,
        val editIntent: EditIntent,
        val toolchain: Toolchain
    ) {
        val mutableShow = MutableShow(baseShow)
        val mutableEditable: MutableEditable = editIntent.findMutableEditable(mutableShow)
        var cachedIsChanged: Boolean? = null

        val uiTitle: String
            get() = "Editing ${mutableEditable.title}"

        fun getEditorPanels(): List<DialogPanel> =
            mutableEditable.getEditorPanels(this@EditableManager)

        fun isChanged(): Boolean {
            return cachedIsChanged
                ?: mutableShow.isChanged(appliedShow!!).also { cachedIsChanged = it }
        }

        fun onChange(pushToUndoStack: Boolean = true) {
            cachedIsChanged = null

            if (pushToUndoStack) {
                undoStack.changed(ShowAndEditIntent(mutableShow.getShow(), editIntent.refreshEditIntent()))
            }
        }
    }

    internal class ShowAndEditIntent(val show: Show, val editIntent: EditIntent)

    companion object {
        private val logger = Logger("EditableManager")
    }
}