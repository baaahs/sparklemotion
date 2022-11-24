package baaahs.app.ui.editor

import baaahs.app.ui.dialog.DialogPanel
import baaahs.show.mutable.MutableDocument
import baaahs.show.mutable.MutablePatchHolder
import baaahs.ui.Facade
import baaahs.util.Logger
import baaahs.util.UndoStack
import kotlin.math.max

abstract class EditableManager<T>(
    private val onApply: (T) -> Unit
) : Facade() {
    private val mutableEditable
        get() = session?.mutableEditable
    val uiTitle: String
        get() = mutableEditable?.title ?: ""

    internal val undoStack = UndoStack<DocAndEditIntent<T>>()
    private var appliedDocument: T? = null
    internal var session: Session? = null

    var isForceExpanded: Boolean
        get() = mutableEditable?.isForceExpanded ?: false
        set(value) { mutableEditable?.isForceExpanded = value }

    fun isEditing(): Boolean = session != null

    fun isModified(): Boolean {
        return session?.isChanged() ?: false
    }

    val currentMutableDocument: MutableDocument<T>
        get() = session!!.mutableDocument

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

    fun maybeChangeTitle(from: String? = null, to: String) {
        (mutableEditable as? MutablePatchHolder)?.maybeChangeTitle(from, to)
    }

    protected fun openEditor(session: Session) {
        if (isEditing()) error("already editing ${this.session!!.editIntent}")

        appliedDocument = session.baseDocument
        this.session = session
        undoStack.reset(DocAndEditIntent(session.baseDocument, session.editIntent))
        selectedPanelIndex = 0
        notifyChanged()
    }

    fun openPanel(dialogPanel: DialogPanel) {
        val editorPanels = flatEditorPanels()
        val index = editorPanels.indexOf(dialogPanel)
        if (index == -1) {
            logger.warn { "Unknown panel $dialogPanel among $editorPanels" }
        }
        selectedPanelIndex = max(0, index)
        notifyChanged()
    }

    /** [DialogPanel]s should call this when they've made a change to the [MutableEditable]. */
    fun onChange(pushToUndoStack: Boolean = true) {
        (session ?: error("No active edit session."))
            .onChange(pushToUndoStack)
        notifyChanged()
    }

    fun undo() = switchBaseShow(undoStack.undo())
    fun canUndo() = undoStack.canUndo()

    fun redo() = switchBaseShow(undoStack.redo())
    fun canRedo() = undoStack.canRedo()

    fun applyChanges() {
        val (newDocument, nextEditIntent) =
            session!!.getNewDocumentAndNextEditIntent()
        appliedDocument = newDocument
        onApply(newDocument)
        switchBaseShow(DocAndEditIntent(newDocument, nextEditIntent))
    }

    private fun switchBaseShow(newDocumentAndEditIntent: DocAndEditIntent<T>) {
        session = session!!.createNewSession(newDocumentAndEditIntent.document, newDocumentAndEditIntent.editIntent)
        notifyChanged()
    }

    fun close() {
        undoStack.reset(null)
        appliedDocument = null
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

    abstract inner class Session(
        val baseDocument: T,
        val mutableDocument: MutableDocument<T>,
        val editIntent: EditIntent,
    ) {
        val mutableEditable: MutableEditable<*> = editIntent.findMutableEditable(mutableDocument)
        var cachedIsChanged: Boolean? = null

        fun getEditorPanels(): List<DialogPanel> =
            mutableEditable.getEditorPanels(this@EditableManager) +
                    editIntent.getEditorPanels(this@EditableManager, mutableEditable)

        fun isChanged(): Boolean {
            return cachedIsChanged
                ?: mutableDocument.isChanged(appliedDocument!!)
                    .also { cachedIsChanged = it }
        }

        fun onChange(pushToUndoStack: Boolean = true) {
            cachedIsChanged = null

            if (pushToUndoStack) {
                undoStack.changed(DocAndEditIntent(mutableDocument.build(), editIntent.refreshEditIntent()))
            }
        }

        fun getNewDocumentAndNextEditIntent(): Pair<T, EditIntent> {
            val newShow = mutableDocument.build()
            val nextEditIntent = editIntent.nextEditIntent()
            return newShow to nextEditIntent
        }
        abstract fun createNewSession(newDocument: T, editIntent: EditIntent): Session
    }

    internal class DocAndEditIntent<T>(val document: T, val editIntent: EditIntent)

    companion object {
        private val logger = Logger("EditableManager")
    }
}