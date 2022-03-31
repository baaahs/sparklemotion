package baaahs.show.mutable

import baaahs.SparkleMotion
import baaahs.app.ui.dialog.DialogPanel
import baaahs.app.ui.editor.*
import baaahs.control.*
import baaahs.show.*

abstract class MutablePatchHolder(
    private val basePatchHolder: PatchHolder
) : MutableEditable<Show> {
    protected abstract val mutableShow: MutableShow

    override var title = basePatchHolder.title

    override fun getEditorPanels(editableManager: EditableManager<*>): List<DialogPanel> {
        return listOf(
            GenericPropertiesEditorPanel(editableManager, getPropertiesComponents()),
            PatchHolderEditorPanel(editableManager, this)
        )
    }

    open fun getPropertiesComponents(): List<PropsEditor> {
        return listOf(TitlePropsEditor(this))
    }

    val patches by lazy {
        basePatchHolder.patches.map { MutablePatch(it, mutableShow) }.toMutableList()
    }
    val eventBindings = basePatchHolder.eventBindings.toMutableList()

    internal val controlLayout by lazy {
        basePatchHolder.controlLayout
            .map { (panelId, controlIds) ->
                mutableShow.findPanel(panelId) to
                        controlIds.map { mutableShow.findControl(it) }.toMutableList()
            }.toMap(mutableMapOf())
    }

    fun addPatch(block: MutablePatch.() -> Unit): MutablePatchHolder {
        val mutablePatch = MutablePatch(
            emptyList(),
            Surfaces.AllSurfaces
        )
        mutablePatch.block()
        addPatch(mutablePatch)
        return this
    }

    fun addPatch(mutablePatch: MutablePatch): MutablePatchHolder {
        val existingPatch = patches.find { it.surfaces == mutablePatch.surfaces }
        if (existingPatch != null) {
            existingPatch.mutableShaderInstances.addAll(mutablePatch.mutableShaderInstances)
            if (SparkleMotion.EXTRA_ASSERTIONS) existingPatch.mutableShaderInstances.assertNoDuplicates()
        } else {
            patches.add(mutablePatch)
        }
        return this
    }

    fun addPatch(mutableShaderInstance: MutableShaderInstance): MutablePatchHolder {
        return addPatch(MutablePatch(listOf(mutableShaderInstance)))
    }

    fun editPatch(index: Int, block: MutablePatch.() -> Unit): MutablePatchHolder {
        patches[index].block()
        return this
    }

    fun addControl(panel: MutablePanel, control: MutableControl) {
        controlLayout.getOrPut(panel) { arrayListOf() }.add(control)
    }

    fun addButton(panel: MutablePanel, title: String, block: MutableButtonControl.() -> Unit): MutableButtonControl {
        val control = MutableButtonControl(ButtonControl(title), mutableShow)
        control.block()
        addControl(panel, control)
        return control
    }

    fun addButtonGroup(
        panel: MutablePanel,
        title: String,
        direction: ButtonGroupControl.Direction = ButtonGroupControl.Direction.Horizontal,
        block: MutableButtonGroupControl.() -> Unit
    ): MutableButtonGroupControl {
        val control = MutableButtonGroupControl(title, direction, mutableListOf(), mutableShow)
        control.block()
        addControl(panel, control)
        return control
    }

    fun addVacuity(
        panel: MutablePanel
    ): MutableVacuityControl {
        val control = MutableVacuityControl("Vacuity")
        addControl(panel, control)
        return control
    }

    fun removeControl(panel: Panel, index: Int): MutableControl {
        return controlLayout.getOrPut(mutableShow.findPanel(panel)) { arrayListOf() }.removeAt(index)
    }

    fun findControlDataSources(): Set<DataSource> {
        return controlLayout.values.flatMap {
            it.filterIsInstance<MutableDataSourcePort>().map { it.dataSource }
        }.toSet()
    }

    fun editControlLayout(panel: Panel): MutableList<MutableControl> {
        return controlLayout.getOrPut(mutableShow.findPanel(panel)) { mutableListOf() }
    }

    internal fun buildControlLayout(showBuilder: ShowBuilder): Map<String, List<String>> {
        return controlLayout.map { (panel, controls) ->
            showBuilder.idFor(panel.build()) to
                    controls.map { mutableControl ->
                        mutableControl.buildAndStashId(showBuilder)
                    }
        }.toMap()
    }

    open fun accept(visitor: MutableShowVisitor, log: VisitationLog = VisitationLog()) {
        if (log.patchHolders.add(this)) visitor.visit(this)
        patches.forEach { it.accept(visitor, log) }
        controlLayout.forEach { (_, controls) ->
            controls.forEach { it.accept(visitor, log) }
        }
    }
}