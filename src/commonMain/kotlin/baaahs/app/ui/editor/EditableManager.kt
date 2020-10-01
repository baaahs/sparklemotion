package baaahs.app.ui.editor

import baaahs.app.ui.EditIntent
import baaahs.app.ui.EditorPanel
import baaahs.app.ui.MutableEditable
import baaahs.show.Show
import baaahs.show.mutable.MutableShow
import baaahs.ui.Facade
import baaahs.util.UndoStack

class EditableManager(
    private val onApply: (Show) -> Unit
) : Facade() {
    internal val undoStack = UndoStack<ShowAndEditIntent>()
    internal var session: Session? = null

    fun isEditing(): Boolean = session != null

    fun isModified(): Boolean {
        return session?.isChanged() ?: false
    }

    val uiTitle: String
        get() = session?.uiTitle ?: ""

    val editorPanels: List<EditorPanel>
        get() = session?.getEditorPanels() ?: emptyList()

    var selectedPanel: EditorPanel? = null

    fun openEditor(baseShow: Show, editIntent: EditIntent) {
        if (isEditing()) error("already editing ${session!!.editIntent}")

        session = Session(baseShow, editIntent)
        undoStack.reset(ShowAndEditIntent(baseShow, editIntent))
        selectedPanel = editorPanels.firstOrNull()
        notifyChanged()
    }

    fun openPanel(editorPanel: EditorPanel) {
        selectedPanel = editorPanel
        notifyChanged()
    }

    /** [EditorPanel]s should call this when they've made a change to the [MutableEditable]. */
    fun onChange() {
        session!!.onChange()
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
        onApply(newShow)
        switchBaseShow(ShowAndEditIntent(newShow, nextEditIntent))
    }

    private fun switchBaseShow(newShowAndEditIntent: ShowAndEditIntent) {
        session = Session(newShowAndEditIntent.show, newShowAndEditIntent.editIntent)
        notifyChanged()
    }

    fun close() {
        undoStack.reset(null)
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
        val baseShow: Show,
        val editIntent: EditIntent
    ) {
        val mutableShow = MutableShow(baseShow)
        val mutableEditable: MutableEditable = editIntent.findMutableEditable(mutableShow)
        var cachedIsChanged: Boolean? = null

        val uiTitle: String
            get() = "Editing ${mutableEditable.title}"

        fun getEditorPanels(): List<EditorPanel> =
            mutableEditable.getEditorPanels()

        fun isChanged(): Boolean {
            return cachedIsChanged
                ?: mutableShow.isChanged().also { cachedIsChanged = it }
        }

        fun onChange() {
            cachedIsChanged = null
            undoStack.changed(ShowAndEditIntent(mutableShow.getShow(), editIntent))
            notifyChanged()
        }
    }

    internal class ShowAndEditIntent(val show: Show, val editIntent: EditIntent)
}