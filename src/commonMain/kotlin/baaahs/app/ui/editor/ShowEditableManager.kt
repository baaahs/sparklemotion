package baaahs.app.ui.editor

import baaahs.gl.Toolchain
import baaahs.show.Show
import baaahs.show.mutable.MutableDocument
import baaahs.show.mutable.MutableShow

class ShowEditableManager(
    onApply: (Show) -> Unit
) : EditableManager<Show>(onApply) {
    fun openEditor(baseDocument: Show, editIntent: EditIntent, toolchain: Toolchain) {
        val session = ShowSession(baseDocument, MutableShow(baseDocument), editIntent, toolchain)
        openEditor(session)
    }

    inner class ShowSession(
        baseDocument: Show,
        mutableDocument: MutableDocument<Show>,
        editIntent: EditIntent,
        val toolchain: Toolchain
    ) : Session(baseDocument, mutableDocument, editIntent) {
        override fun createNewSession(newDocument: Show, editIntent: EditIntent): Session {
            return ShowSession(newDocument, MutableShow(newDocument), editIntent, toolchain)
        }
    }
}