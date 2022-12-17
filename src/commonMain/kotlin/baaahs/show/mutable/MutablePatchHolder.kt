package baaahs.show.mutable

import baaahs.app.ui.dialog.DialogPanel
import baaahs.app.ui.editor.*
import baaahs.control.*
import baaahs.show.*

abstract class MutablePatchHolder(
    private val basePatchHolder: PatchHolder
) : MutableEditable<Show> {
    protected abstract val mutableShow: MutableShow

    override var title = basePatchHolder.title

    override var isForceExpanded: Boolean = false

    override fun getEditorPanels(editableManager: EditableManager<*>): List<DialogPanel> =
        when {
            patches.isEmpty() && !isForceExpanded -> {
                listOf(PatchesOverviewPanel(editableManager, this))
            }
            patches.size == 1 && !isForceExpanded -> {
                listOf(SingleShaderSimplifiedEditorPanel(editableManager, this))
            }
            else -> {
                listOf(
                    GenericPropertiesEditorPanel(editableManager, getPropertiesComponents()),
                    PatchesOverviewPanel(editableManager, this)
                )
            }
        }

    open fun getPropertiesComponents(): List<PropsEditor> {
        return listOf(TitlePropsEditor(this))
    }

    val patches by lazy {
        basePatchHolder.patchIds.map { mutableShow.findPatch(it) }.toMutableList()
    }
    val eventBindings = basePatchHolder.eventBindings.toMutableList()

    internal val controlLayout by lazy {
        basePatchHolder.controlLayout
            .map { (panelId, controlIds) ->
                mutableShow.findPanel(panelId) to
                        controlIds.map { mutableShow.findControl(it) }.toMutableList()
            }.toMap(mutableMapOf())
    }

    fun maybeChangeTitle(from: String?, to: String) {
        if (title.isEmpty() ||
            title == from ||
            title == "New Button" // TODO: This is dumb, DRY or allow title to be null?
        ) {
            title = to
        }
    }

    fun addPatch(shader: Shader, block: MutablePatch.() -> Unit = {}): MutablePatchHolder =
        addPatch(
            MutablePatch(MutableShader(shader))
            .also { it.block() }
        )

    fun addPatch(shader: MutableShader, block: MutablePatch.() -> Unit = {}): MutablePatchHolder =
        addPatch(
            MutablePatch(shader)
                .also { it.block() }
        )

    fun addPatch(block: MutablePatch.() -> Unit): MutablePatchHolder =
        addPatch(
            MutablePatch(MutableShader(Shader("", "")))
                .also { it.block() }
        )

    fun addPatch(mutablePatchSet: MutablePatchSet): MutablePatchHolder {
        patches.addAll(mutablePatchSet.mutablePatches)
        return this
    }

    fun addPatch(mutablePatch: MutablePatch): MutablePatchHolder {
        patches.add(mutablePatch)
        return this
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
        val control = MutableButtonGroupControl(title, direction, mutableShow = mutableShow)
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

    fun findControlFeeds(): Set<Feed> {
        return controlLayout.values.flatMap {
            it.filterIsInstance<MutableFeedPort>().map { it.feed }
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