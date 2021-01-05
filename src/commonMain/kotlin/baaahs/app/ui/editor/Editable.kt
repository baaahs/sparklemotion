package baaahs.app.ui

import baaahs.Severity
import baaahs.app.ui.editor.EditableManager
import baaahs.show.ButtonControl
import baaahs.show.live.ControlDisplay
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
    fun getEditorPanels(editableManager: EditableManager): List<EditorPanel>
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

abstract class AddToContainerEditIntent<T: MutableControl> : EditIntent {
    private lateinit var mutableEditable: T

    abstract fun createControl(mutableShow: MutableShow): T

    abstract fun addToContainer(mutableShow: MutableShow, mutableControl: T)

    override fun findMutableEditable(mutableShow: MutableShow): MutableEditable {
        mutableEditable = createControl(mutableShow)
        addToContainer(mutableShow, mutableEditable)
        return mutableEditable
    }

    override fun nextEditIntent(): EditIntent {
        return ControlEditIntent(mutableEditable.asBuiltId!!)
    }
}

data class AddButtonToButtonGroupEditIntent(
    private val containerId: String
) : AddToContainerEditIntent<MutableButtonControl>() {
    override fun createControl(mutableShow: MutableShow): MutableButtonControl {
        return MutableButtonControl(ButtonControl("New Button"), mutableShow)
    }

    override fun addToContainer(mutableShow: MutableShow, mutableControl: MutableButtonControl) {
        val container = mutableShow.findControl(containerId) as MutableButtonGroupControl
        container.buttons.add(mutableControl)
    }
}

class AddControlToPanelBucket<MC : MutableControl>(
    private val panelBucket: ControlDisplay.PanelBuckets.PanelBucket,
    private val createControlFn: (mutableShow: MutableShow) -> MC
) : AddToContainerEditIntent<MC>() {
    override fun createControl(mutableShow: MutableShow): MC {
        return createControlFn(mutableShow)
    }

    override fun addToContainer(mutableShow: MutableShow, mutableControl: MC) {
        mutableShow.findPatchHolder(panelBucket.section.container)
            .editControlLayout(panelBucket.panelTitle)
            .add(mutableControl)
    }
}

interface EditorPanel {
    val title: String
    val listSubhead: String?
    val icon: Icon?
    val problemLevel: Severity? get() = null

    fun getNestedEditorPanels(): List<EditorPanel> = emptyList()
    fun getRenderer(editableManager: EditableManager): Renderer
}
