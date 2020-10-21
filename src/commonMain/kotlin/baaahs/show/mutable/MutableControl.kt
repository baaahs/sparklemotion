package baaahs.show.mutable

import baaahs.Gadget
import baaahs.app.ui.EditorPanel
import baaahs.app.ui.MutableEditable
import baaahs.app.ui.editor.*
import baaahs.camelize
import baaahs.randomId
import baaahs.show.*
import baaahs.show.live.OpenControl
import baaahs.show.live.OpenGadgetControl

interface MutableControl : MutableEditable {
    var asBuiltId: String?
    fun build(showBuilder: ShowBuilder): Control
    fun accept(visitor: MutableShowVisitor, log: VisitationLog) {
        if (log.controls.add(this)) visitor.visit(this)
    }

    fun buildAndStashId(showBuilder: ShowBuilder): String {
        return showBuilder.idFor(build(showBuilder))
            .also { asBuiltId = it }
    }
}

data class MutableGadgetControl(
    var gadget: Gadget,
    val controlledDataSource: DataSource
) : MutableControl {
    override val title: String
        get() = gadget.title

    override var asBuiltId: String? = null

    override fun getEditorPanels(): List<EditorPanel> {
        return emptyList()
    }

    override fun build(showBuilder: ShowBuilder): Control {
        return GadgetControl(gadget, showBuilder.idFor(controlledDataSource))
    }

    fun open(): OpenControl {
        return OpenGadgetControl(randomId(gadget.title.camelize()), gadget, controlledDataSource)
    }
}

class MutableButtonControl(
    baseButtonControl: ButtonControl,
    override val mutableShow: MutableShow
) : MutablePatchHolder(baseButtonControl), MutableControl {
    var activationType: ButtonControl.ActivationType = baseButtonControl.activationType

    override var asBuiltId: String? = null

    override fun getPropertiesComponents(): List<PropsEditor> {
        return super.getPropertiesComponents() + ButtonPropsEditor(this)
    }

    override fun build(showBuilder: ShowBuilder): Control {
        return ButtonControl(
            title,
            activationType,
            patches = patches.map { it.build(showBuilder) },
            eventBindings = eventBindings,
            controlLayout = buildControlLayout(showBuilder)
        )
    }

    override fun accept(visitor: MutableShowVisitor, log: VisitationLog) {
        super<MutablePatchHolder>.accept(visitor, log)
        super<MutableControl>.accept(visitor, log)
    }
}

data class MutableButtonGroupControl(
    override var title: String,
    var direction: ButtonGroupControl.Direction,
    val buttons: MutableList<MutableButtonControl> = arrayListOf(),
    val mutableShow: MutableShow
) : MutableControl {
    override var asBuiltId: String? = null

    fun addButton(title: String, block: MutableButtonControl.() -> Unit): MutableButtonControl {
        val control = MutableButtonControl(ButtonControl(title), mutableShow)
        control.block()
        buttons.add(control)
        return control
    }

    override fun getEditorPanels(): List<EditorPanel> {
        return listOf(
            GenericPropertiesEditorPanel(
                ButtonGroupPropsEditor(this)
            )
        )
    }

    override fun build(showBuilder: ShowBuilder): Control {
        return ButtonGroupControl(title, direction, buttons.map { mutableButtonControl ->
            mutableButtonControl.buildAndStashId(showBuilder)
        })
    }

    override fun accept(visitor: MutableShowVisitor, log: VisitationLog) {
        super.accept(visitor, log)
        buttons.forEach { it.accept(visitor, log) }
    }

    fun moveButton(fromIndex: Int, toIndex: Int) {
        buttons.add(toIndex, buttons.removeAt(fromIndex))
    }
}

data class MutableVisualizerControl(
    var surfaceDisplayMode: VisualizerControl.SurfaceDisplayMode = VisualizerControl.SurfaceDisplayMode.Continuous,
    var rotate: Boolean = false
) : MutableControl {
    override val title: String get() = "Visualizer"

    override var asBuiltId: String? = null

    override fun getEditorPanels(): List<EditorPanel> {
        return listOf(
            GenericPropertiesEditorPanel(
                VisualizerPropsEditor(this)
            )
        )
    }

    override fun build(showBuilder: ShowBuilder): Control {
        return VisualizerControl(surfaceDisplayMode, rotate)
    }
}
