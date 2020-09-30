package baaahs.app.ui

import baaahs.app.ui.editor.EditableManager
import baaahs.show.ButtonControl
import baaahs.show.mutable.MutableButtonControl
import baaahs.show.mutable.MutableButtonGroupControl
import baaahs.show.mutable.MutableShow
import baaahs.ui.Icon
import baaahs.ui.Renderer

interface Editable {
    val title: String
}

interface MutableEditable {
    val title: String
    fun getEditorPanels(): List<EditorPanel>
}

interface EditIntent {
    fun findMutableEditable(mutableShow: MutableShow): MutableEditable
    fun nextEditIntent(): EditIntent = this
}

class ShowEditIntent : EditIntent {

    override fun findMutableEditable(mutableShow: MutableShow): MutableEditable =
        mutableShow
}

data class ControlEditIntent(private val controlId: String) : EditIntent {

    override fun findMutableEditable(mutableShow: MutableShow): MutableEditable =
        mutableShow.findControl(controlId)
}

data class AddButtonToButtonGroupEditIntent(private val containerId: String) : EditIntent {
    private lateinit var mutableEditable: MutableButtonControl

    override fun findMutableEditable(mutableShow: MutableShow): MutableEditable {
        val container = mutableShow.findControl(containerId) as MutableButtonGroupControl
        mutableEditable = MutableButtonControl(ButtonControl("New Button"), mutableShow)
        container.buttons.add(mutableEditable)
        return mutableEditable
    }

    override fun nextEditIntent(): EditIntent {
        return ControlEditIntent(mutableEditable.asBuiltId!!)
    }
}

interface EditorPanel {
    val title: String
    val listSubhead: String?
    val icon: Icon?
    fun getNestedEditorPanels(): List<EditorPanel> = emptyList()
    fun getRenderer(editableManager: EditableManager): Renderer
}
