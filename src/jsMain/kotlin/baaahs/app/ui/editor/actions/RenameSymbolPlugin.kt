package baaahs.app.ui.editor.actions

import acex.Editor
import acex.Selection
import baaahs.app.ui.AppContext
import baaahs.show.mutable.EditingShader

class RenameSymbolPlugin(
    private val editingShader: EditingShader,
    private val editor: Editor,
    private val appContext: AppContext
): EditorPlugin {
    override fun findActions(selection: Selection): List<EditorPlugin.Action> {
        return emptyList()
    }
}