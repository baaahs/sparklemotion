package baaahs.show.mutable

import baaahs.*
import baaahs.app.ui.EditorPanel
import baaahs.app.ui.MutableEditable
import baaahs.app.ui.editor.*
import baaahs.gl.patch.AutoWirer
import baaahs.gl.patch.ContentType
import baaahs.gl.patch.LinkedPatch
import baaahs.gl.shader.OpenShader
import baaahs.show.*
import baaahs.show.live.*
import baaahs.util.CacheBuilder
import baaahs.util.UniqueIds

class PatchHolderEditContext(
    val mutableShow: MutableShow,
    val mutablePatchHolder: MutablePatchHolder
)

interface EditHandler {
    fun onShowEdit(mutableShow: MutableShow, pushToUndoStack: Boolean = true)
    fun onShowEdit(show: Show, pushToUndoStack: Boolean = true)
    fun onShowEdit(show: Show, showState: ShowState, pushToUndoStack: Boolean = true)
}

abstract class MutablePatchHolder(
    private val basePatchHolder: PatchHolder
) : MutableEditable {
    protected abstract val mutableShow: MutableShow

    override var title = basePatchHolder.title

    override fun getEditorPanels(): List<EditorPanel> {
        return listOf(
            GenericPropertiesEditorPanel(getPropertiesComponents()),
            PatchHolderEditorPanel(this))
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
            .mapValues { (_, v) ->
                v.map { mutableShow.findControl(it) }.toMutableList()
            }.toMutableMap()
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
        } else {
            patches.add(mutablePatch)
        }
        return this
    }

    fun editPatch(index: Int, block: MutablePatch.() -> Unit): MutablePatchHolder {
        patches[index].block()
        return this
    }

    fun addControl(panel: String, control: MutableControl) {
        controlLayout.getOrPut(panel) { arrayListOf() }.add(control)
    }

    fun addButton(panel: String, title: String, block: MutableButtonControl.() -> Unit): MutableButtonControl {
        val control = MutableButtonControl(ButtonControl(title), mutableShow)
        control.block()
        addControl(panel, control)
        return control
    }

    fun addButtonGroup(
        panel: String,
        title: String,
        direction: ButtonGroupControl.Direction = ButtonGroupControl.Direction.Horizontal,
        block: MutableButtonGroupControl.() -> Unit
    ): MutableButtonGroupControl {
        val control = MutableButtonGroupControl(title, direction, mutableListOf(), mutableShow)
        control.block()
        addControl(panel, control)
        return control
    }

    fun removeControl(panel: String, index: Int): MutableControl {
        return controlLayout.getOrPut(panel) { arrayListOf() }.removeAt(index)
    }

    fun findControlDataSources(): Set<DataSource> {
        return controlLayout.values.flatMap {
            it.filterIsInstance<MutableDataSource>().map { it.dataSource }
        }.toSet()
    }

    fun editControlLayout(panelName: String): MutableList<MutableControl> {
        return controlLayout.getOrPut(panelName) { mutableListOf() }
    }

    internal fun buildControlLayout(showBuilder: ShowBuilder): Map<String, List<String>> {
        return controlLayout.mapValues { (_, v) ->
            v.map { mutableControl ->
                mutableControl.buildAndStashId(showBuilder)
            }
        }
    }
}

class MutableShow(
    baseShow: Show
) : MutablePatchHolder(baseShow), MutableEditable {
    override val mutableShow: MutableShow get() = this

    internal val controls = CacheBuilder<String, MutableControl> { id ->
        baseShow.controls.getBang(id, "control").createMutable(this)
    }

    internal val dataSources = baseShow.dataSources
        .mapValues { (_, shader) -> MutableDataSource(shader) }
        .toMutableMap()

    internal val shaders = baseShow.shaders
        .mapValues { (_, shader) -> MutableShader(shader) }
        .toMutableMap()

    val shaderChannels = mutableSetOf<ShaderChannel>()

    val shaderInstances = baseShow.shaderInstances
        .mapValues { (_, shaderInstance) ->
            shaderChannels.add(shaderInstance.shaderChannel)

            MutableShaderInstance(
                findShader(shaderInstance.shaderId),
                hashMapOf(),
                shaderInstance.shaderChannel,
                shaderInstance.priority
            )
        }.toMutableMap()

    init {
        // Second pass required here since they might refer to each other.
        baseShow.shaderInstances.forEach { (id, shaderInstance) ->
            val editor = shaderInstances.getBang(id, "shader instance")
            val resolvedIncomingLinks = shaderInstance.incomingLinks.mapValues { (_, fromPortRef) ->
                fromPortRef.dereference(this)
                    .also { it.collectShaderChannels(shaderChannels) }
            }
            editor.incomingLinks.putAll(resolvedIncomingLinks)
        }
    }

    private val mutableLayouts = MutableLayouts(baseShow.layouts)

    constructor(title: String, block: MutableShow.() -> Unit = {}) : this(Show(title)) {
        this.block()
    }

    fun invoke(block: MutableShow.() -> Unit) = this.block()

    fun editLayouts(block: MutableLayouts.() -> Unit): MutableShow {
        mutableLayouts.apply(block)
        return this
    }

    fun isChanged(baseShow: Show): Boolean {
        return baseShow != getShow()
    }

    fun build(showBuilder: ShowBuilder): Show {
        return Show(
            title,
            patches = patches.map { it.build(showBuilder) },
            eventBindings = eventBindings,
            controlLayout = buildControlLayout(showBuilder),
            layouts = mutableLayouts.build(),
            shaders = showBuilder.getShaders(),
            shaderInstances = showBuilder.getShaderInstances(),
            dataSources = showBuilder.getDataSources(),
            controls = showBuilder.getControls()
        )
    }

    fun getShow() = build(ShowBuilder())

    fun findControl(controlId: String): MutableControl =
        controls.getBang(controlId, "control")

    fun edit(buttonGroupControl: OpenButtonGroupControl, block: MutableButtonGroupControl.() -> Unit = {}) {
        (findControl(buttonGroupControl.id) as MutableButtonGroupControl).block()
    }

    fun findDataSource(dataSourceId: String): MutableDataSource =
        dataSources.getBang(dataSourceId, "data source")

    fun findPatchHolder(openPatchHolder: OpenPatchHolder): MutablePatchHolder {
        return when (openPatchHolder) {
            is OpenShow -> this

            // Yuck.
            is OpenButtonControl -> return findControl(openPatchHolder.id) as MutableButtonControl

            else -> error("huh? $openPatchHolder isn't a show or a button?")
        }
    }

    fun findShader(shaderId: String): MutableShader =
        shaders.getBang(shaderId, "shader")

    fun findShaderInstance(id: String): MutableShaderInstance =
        shaderInstances.getBang(id, "shader instance")

    fun commit(editHandler: EditHandler) {
        editHandler.onShowEdit(this)
    }

    companion object {
        fun create(title: String): Show {
            return Show(title = title)
        }
    }
}

class MutableLayouts(baseLayouts: Layouts) {
    var panelNames = baseLayouts.panelNames.toMutableList()
    val map = baseLayouts.map.toMutableMap()

    fun copyFrom(layouts: Layouts) {
        panelNames.clear()
        panelNames.addAll(layouts.panelNames)

        map.clear()
        map.putAll(layouts.map)
    }

    fun build(): Layouts {
        return Layouts(panelNames, map)
    }
}

class MutablePatch {
    val id: String = randomId("patch-editor")

    val mutableShaderInstances: MutableList<MutableShaderInstance>
    var surfaces: Surfaces

    constructor(
        mutableShaderInstances: List<MutableShaderInstance> = emptyList(),
        surfaces: Surfaces = Surfaces.AllSurfaces
    ) {
        this.mutableShaderInstances = mutableShaderInstances.toMutableList()
        this.surfaces = surfaces
    }

    constructor(basePatch: Patch, show: MutableShow) {
        this.mutableShaderInstances = basePatch.shaderInstanceIds.map { shaderInstanceId ->
            show.findShaderInstance(shaderInstanceId)
        }.toMutableList()

        this.surfaces = basePatch.surfaces
    }

    constructor(block: MutablePatch.() -> Unit = {}) {
        this.mutableShaderInstances = arrayListOf()
        this.surfaces = Surfaces.AllSurfaces

        block()
    }

    fun findDataSources(): Set<DataSource> =
        mutableShaderInstances.flatMap { it.findDataSources() }.toSet()

    fun findShaderInstances(): Set<MutableShaderInstance> =
        mutableShaderInstances.toSet()

    fun findShaderChannels(): List<ShaderChannel> {
        return findShaderInstances().flatMap { shaderInstanceEditor ->
            shaderInstanceEditor.findShaderChannels()
        }
    }

    fun build(showBuilder: ShowBuilder): Patch =
        Patch(
            mutableShaderInstances.map { showBuilder.idFor(it.build(showBuilder)) },
            surfaces
        )

    /** Build a [LinkedPatch] independent of an [baaahs.show.live.OpenShow]. */
    fun openForPreview(autoWirer: AutoWirer): LinkedPatch? {
        val showBuilder = ShowBuilder()
        build(showBuilder)

        val openShaders = CacheBuilder<String, OpenShader> { shaderId ->
            autoWirer.glslAnalyzer.openShader(showBuilder.getShaders().getBang(shaderId, "shader"))
        }

        val resolvedShaderInstances =
            ShaderInstanceResolver(openShaders, showBuilder.getShaderInstances(), showBuilder.getDataSources())
                .getResolvedShaderInstances()
        val openPatch = OpenPatch(resolvedShaderInstances.values.toList(), surfaces)

        val portDiagram = autoWirer.buildPortDiagram(openPatch)
        return portDiagram.resolvePatch(ShaderChannel.Main, ContentType.ColorStream)
    }

    fun addShaderInstance(mutableShaderInstance: MutableShaderInstance): MutablePatch {
        mutableShaderInstances.add(mutableShaderInstance)
        return this
    }

    fun addShaderInstance(shader: Shader, block: MutableShaderInstance.() -> Unit): MutableShaderInstance {
        val mutableShaderInstance = MutableShaderInstance(MutableShader(shader))
        mutableShaderInstance.block()
        mutableShaderInstances.add(mutableShaderInstance)
        return mutableShaderInstance
    }

    fun findShaderInstanceFor(shader: Shader): MutableShaderInstance {
        return mutableShaderInstances.find { it.mutableShader.build() == shader }
            ?: error("No shader instance for ${shader.title}.")
    }

    fun remove(mutableShaderInstance: MutableShaderInstance) {
        mutableShaderInstances.remove(mutableShaderInstance)
    }

    fun getEditorPanel() = PatchEditorPanel(this)
}

interface MutablePort {
    fun toRef(showBuilder: ShowBuilder): PortRef
    fun displayName(): String
    fun collectShaderChannels(shaderChannels: MutableSet<ShaderChannel>) = Unit
}

data class MutableShaderChannel(val shaderChannel: ShaderChannel) : MutablePort {
    override fun toRef(showBuilder: ShowBuilder): PortRef =
        ShaderChannelRef(shaderChannel)

    override fun displayName(): String =
        "channel(${shaderChannel.id})"

    override fun collectShaderChannels(shaderChannels: MutableSet<ShaderChannel>) {
        shaderChannels.add(shaderChannel)
    }
}

data class MutableDataSource(val dataSource: DataSource) : MutablePort {
    override fun toRef(showBuilder: ShowBuilder): PortRef =
        DataSourceRef(showBuilder.idFor(dataSource))

    override fun displayName(): String = dataSource.dataSourceName
}

fun DataSource.editor() = MutableDataSource(this)

interface MutableControl : MutableEditable {
    var asBuiltId: String?
    fun build(showBuilder: ShowBuilder): Control
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

    fun moveButton(fromIndex: Int, toIndex: Int) {
        buttons.add(toIndex, buttons.removeAt(fromIndex))
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

data class MutableShader(
    var title: String,
    var type: ShaderType,
    /**language=glsl*/
    var src: String
) {
    constructor(shader: Shader) : this(shader.title, shader.type, shader.src)

    fun build(): Shader {
        return Shader(title, type, src)
    }
}

data class MutableShaderInstance(
    val mutableShader: MutableShader,
    val incomingLinks: MutableMap<String, MutablePort> = hashMapOf(),
    var shaderChannel: ShaderChannel = ShaderChannel.Main,
    var priority: Float = 0f
) {
    val id = randomId("MutableShaderInstance")

    fun findDataSources(): List<DataSource> {
        return incomingLinks.mapNotNull { (_, from) ->
            (from as? MutableDataSource)?.dataSource
        }
    }

    fun findShaderChannels(): List<ShaderChannel> {
        return (incomingLinks.values.map { link ->
            if (link is MutableShaderChannel) link.shaderChannel else null
        } + shaderChannel).filterNotNull()
    }

    fun link(portId: String, toPort: DataSource) {
        incomingLinks[portId] = toPort.editor()
    }

    fun link(portId: String, toPort: MutablePort) {
        incomingLinks[portId] = toPort
    }

    fun build(showBuilder: ShowBuilder): ShaderInstance {
        return ShaderInstance(
            showBuilder.idFor(mutableShader.build()),
            incomingLinks.mapValues { (_, portRef) ->
                portRef.toRef(showBuilder)
            },
            shaderChannel,
            priority
        )
    }

    fun getEditorPanel(patchEditorPanel: PatchEditorPanel): EditorPanel =
        patchEditorPanel.ShaderInstanceEditorPanel(this)

    companion object {
        val defaultOrder = compareBy<MutableShaderInstance>(
            { it.mutableShader.type.priority },
            { it.mutableShader.title }
        )
    }
}

data class MutableShaderOutPort(var mutableShaderInstance: MutableShaderInstance) : MutablePort {
    override fun toRef(showBuilder: ShowBuilder): PortRef =
        ShaderOutPortRef(showBuilder.idFor(mutableShaderInstance.build(showBuilder)))

    override fun displayName(): String = "Shader \"${mutableShaderInstance.mutableShader.title}\" output"

    override fun toString(): String = "ShaderOutPortEditor(shader=${mutableShaderInstance.mutableShader.title})"
}

data class MutableOutputPort(private val portId: String) : MutablePort {
    override fun toRef(showBuilder: ShowBuilder): PortRef =
        OutputPortRef(portId)

    override fun displayName(): String = "$portId Output"
}

data class MutableConstPort(private val glsl: String) : MutablePort {
    override fun toRef(showBuilder: ShowBuilder): PortRef =
        ConstPortRef(glsl)

    override fun displayName(): String = "const($glsl)"
}

abstract class MutableShowVisitor {
    open fun visitShow(mutableShow: MutableShow) {
        visitPatchHolder(mutableShow)
    }

    open fun visitPatchHolder(mutablePatchHolder: MutablePatchHolder) {
        mutablePatchHolder.patches.forEach {
            visitPatch(it)
        }

        mutablePatchHolder.controlLayout.forEach { (panelName, mutableControls) ->
            mutableControls.forEach { mutableControl ->
                visitPlacedControl(panelName, mutableControl)
            }
        }
    }

    open fun visitPlacedControl(panelName: String, mutableControl: MutableControl) {
        if (mutableControl is MutablePatchHolder) {
            visitPatchHolder(mutableControl)
        }

        if (mutableControl is ControlContainer) {
            visitControlContainer(mutableControl)
        }
    }

    open fun visitControlContainer(mutableControl: ControlContainer) {
        mutableControl.containedControls().forEach { containedControl ->
            if (containedControl.isActive() && containedControl is MutablePatchHolder) {
                visitPatchHolder(containedControl)
            }
        }
    }

    open fun visitPatch(mutablePatch: MutablePatch) {
        mutablePatch.mutableShaderInstances.forEach {
            visitShaderInstance(it)
        }
    }

    open fun visitShaderInstance(mutableShaderInstance: MutableShaderInstance) {
    }
}


private fun MutableControl.buildAndStashId(showBuilder: ShowBuilder): String {
    return showBuilder.idFor(build(showBuilder))
        .also { asBuiltId = it }
}


class ShowBuilder {
    private val controlIds = UniqueIds<Control>()
    private val dataSourceIds = UniqueIds<DataSource>()
    private val shaderIds = UniqueIds<Shader>()
    private val shaderInstanceIds = UniqueIds<ShaderInstance>()

    fun idFor(control: Control): String {
        return controlIds.idFor(control) { control.suggestId() }
    }

    fun idFor(dataSource: DataSource): String {
        return dataSourceIds.idFor(dataSource) { dataSource.suggestId() }
    }

    fun idFor(shader: Shader): String {
        return shaderIds.idFor(shader) { shader.suggestId() }
    }

    fun idFor(shaderInstance: ShaderInstance): String {
        return shaderInstanceIds.idFor(shaderInstance) { "${shaderInstance.shaderId}-inst" }
    }

    fun getControls(): Map<String, Control> = controlIds.all()
    fun getDataSources(): Map<String, DataSource> = dataSourceIds.all()
    fun getShaders(): Map<String, Shader> = shaderIds.all()
    fun getShaderInstances(): Map<String, ShaderInstance> = shaderInstanceIds.all()
}