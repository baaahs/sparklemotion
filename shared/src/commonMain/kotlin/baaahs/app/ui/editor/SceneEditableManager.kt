package baaahs.app.ui.editor

import baaahs.scene.MutableScene
import baaahs.scene.Scene
import baaahs.show.mutable.MutableDocument

class SceneEditableManager(
    onApply: (Scene) -> Unit
) : EditableManager<Scene>(onApply) {
    fun openEditor(baseDocument: Scene, editIntent: EditIntent) {
        val session = SceneSession(baseDocument, MutableScene(baseDocument), editIntent)
        openEditor(session)
    }

    inner class SceneSession(
        baseDocument: Scene,
        mutableDocument: MutableDocument<Scene>,
        editIntent: EditIntent
    ) : Session(baseDocument, mutableDocument, editIntent) {
        override fun createNewSession(newDocument: Scene, editIntent: EditIntent): Session {
            return SceneSession(newDocument, MutableScene(newDocument), editIntent)
        }
    }
}