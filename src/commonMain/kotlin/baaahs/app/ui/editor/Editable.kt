package baaahs.app.ui

import baaahs.app.ui.editor.EditableManager
import baaahs.show.ButtonControl
import baaahs.show.mutable.MutableButtonControl
import baaahs.show.mutable.MutableButtonGroupControl
import baaahs.show.mutable.MutableControl
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

    /**
     * If a mutation might have changed how we should look up the editable, a new
     * version of the same EditIntent can be provided here. This is called when pushing
     * to the [EditableManager]'s [baaahs.util.UndoStack].
     */
    fun refreshEditIntent(): EditIntent = this

    /**
     * If applying [EditableManager] changes performs an action like adding a new control,
     * and further edits should modify that control but not add yet another one, a new
     * EditIntent for subsequent edits can be provided here.
     */
    fun nextEditIntent(): EditIntent = this
}

class ShowEditIntent : EditIntent {
    override fun findMutableEditable(mutableShow: MutableShow): MutableEditable =
        mutableShow
}

data class ControlEditIntent(internal val controlId: String) : EditIntent {
    private lateinit var mutableEditable: MutableControl

    override fun findMutableEditable(mutableShow: MutableShow): MutableEditable {
        mutableEditable = mutableShow.findControl(controlId)
        return mutableEditable
    }

    override fun refreshEditIntent(): EditIntent {
        return copy(controlId = mutableEditable.asBuiltId!!)
    }

    override fun nextEditIntent(): EditIntent {
        return ControlEditIntent(mutableEditable.asBuiltId!!)
    }
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
