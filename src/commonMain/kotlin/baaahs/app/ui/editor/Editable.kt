package baaahs.app.ui.editor

import baaahs.app.ui.dialog.DialogPanel
import baaahs.control.ButtonControl
import baaahs.control.MutableButtonControl
import baaahs.control.MutableButtonGroupControl
import baaahs.show.live.ControlDisplay
import baaahs.show.mutable.*

interface Editable {
    val title: String
}

interface MutableEditable<T> {
    val title: String
    var isForceExpanded: Boolean
        get() = false
        set(_) {}

    fun getEditorPanels(editableManager: EditableManager<*>): List<DialogPanel>
}

interface EditIntent {
    fun findMutableEditable(mutableDocument: MutableDocument<*>): MutableEditable<*>

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
    override fun findMutableEditable(mutableDocument: MutableDocument<*>): MutableEditable<*> =
        mutableDocument
}

class SceneEditIntent : EditIntent {
    override fun findMutableEditable(mutableDocument: MutableDocument<*>): MutableEditable<*> =
        mutableDocument
}

data class ControlEditIntent(internal val controlId: String) : EditIntent {
    private lateinit var mutableEditable: MutableControl

    override fun findMutableEditable(mutableDocument: MutableDocument<*>): MutableEditable<*> {
        mutableEditable = (mutableDocument as MutableShow).findControl(controlId)
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

    override fun findMutableEditable(mutableDocument: MutableDocument<*>): MutableEditable<*> {
        mutableDocument as MutableShow
        mutableEditable = createControl(mutableDocument)
        addToContainer(mutableDocument, mutableEditable)
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
            .editControlLayout(panelBucket.panel)
            .add(mutableControl)
    }
}

class AddControlToGrid<MC : MutableControl>(
    private val editor: Editor<MutableGridTab>,
    private val column: Int,
    private val row: Int,
    private val width: Int,
    private val height: Int,
    private val createControlFn: (mutableShow: MutableShow) -> MC
) : AddToContainerEditIntent<MC>() {
    override fun createControl(mutableShow: MutableShow): MC {
        return createControlFn(mutableShow)
    }

    override fun addToContainer(mutableShow: MutableShow, mutableControl: MC) {
        editor.edit(mutableShow) {
            items.add(MutableGridItem(mutableControl, column, row, 1, 1))
        }
    }
}

interface Editor<T> {
    fun edit(mutableShow: MutableShow, block: T.() -> Unit)
}