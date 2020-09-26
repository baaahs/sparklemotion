package baaahs.app.ui

import baaahs.app.ui.editor.EditableManager
import baaahs.show.Show
import baaahs.show.mutable.MutableShow
import baaahs.ui.Icon
import baaahs.ui.Renderer

interface Editable {
    val title: String
}

interface MutableEditable {
    fun getEditorPanels(): List<EditorPanel>
}

interface EditIntent {
    fun findEditable(baseShow: Show): Editable
    fun findMutableEditable(mutableShow: MutableShow): MutableEditable
}

class ShowEditIntent : EditIntent {
    override fun findEditable(baseShow: Show): Editable =
        baseShow

    override fun findMutableEditable(mutableShow: MutableShow): MutableEditable =
        mutableShow
}

class ControlEditIntent(val controlId: String) : EditIntent {
    override fun findEditable(baseShow: Show): Editable =
        baseShow.getControl(controlId)

    override fun findMutableEditable(mutableShow: MutableShow): MutableEditable =
        mutableShow.findControl(controlId)
}

class AddButtonToContainerEditIntent(val containerId: String) : EditIntent {
    override fun findEditable(baseShow: Show): Editable =
        TODO() // baseShow.getControl(controlId)

    override fun findMutableEditable(mutableShow: MutableShow): MutableEditable {
        TODO () // baseShow.getControl(controlId)
//            .toMutable(MutableShow(baseShow))
    }
}

interface EditorPanel {
    val title: String
    val listSubhead: String?
    val icon: Icon?
    fun getNestedEditorPanels(): List<EditorPanel> = emptyList()
    fun getRenderer(editableManager: EditableManager): Renderer
}
