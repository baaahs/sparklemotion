package baaahs.show.mutable

import baaahs.SparkleMotion
import baaahs.app.ui.dialog.DialogPanel
import baaahs.app.ui.editor.*
import baaahs.control.*
import baaahs.getBang
import baaahs.getValue
import baaahs.gl.Toolchain
import baaahs.gl.openShader
import baaahs.gl.patch.ContentType
import baaahs.gl.patch.LinkedPatch
import baaahs.gl.patch.PatchResolver
import baaahs.gl.shader.OpenShader
import baaahs.randomId
import baaahs.show.*
import baaahs.show.live.OpenPatch
import baaahs.show.live.OpenPatchHolder
import baaahs.show.live.OpenShow
import baaahs.show.live.ShaderInstanceResolver
import baaahs.util.CacheBuilder
import baaahs.util.UniqueIds

interface EditHandler<T, TState> {
    fun onEdit(mutableDocument: MutableDocument<T>, pushToUndoStack: Boolean = true)
    fun onEdit(document: T, pushToUndoStack: Boolean = true)
    fun onEdit(document: T, documentState: TState, pushToUndoStack: Boolean = true, syncToServer: Boolean = true)
}

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

interface MutableDocument<T> : MutableEditable<T> {
    fun build(): T

    fun isChanged(originalDocument: T): Boolean =
        originalDocument != build()
}

class MutableShow(
    baseShow: Show
) : MutablePatchHolder(baseShow), MutableDocument<Show> {
    override val mutableShow: MutableShow get() = this

    internal val layouts = MutableLayouts(baseShow.layouts)

    internal val controls = CacheBuilder<String, MutableControl> { id ->
        baseShow.controls.getBang(id, "control").createMutable(this)
    }

    internal val dataSources = baseShow.dataSources
        .mapValues { (_, shader) -> MutableDataSourcePort(shader) }
        .toMutableMap()

    internal val shaders = baseShow.shaders
        .mapValues { (_, shader) -> MutableShader(shader) }
        .toMutableMap()

    private val shaderChannels = CacheBuilder<ShaderChannel, MutableShaderChannel> {
        MutableShaderChannel(it.id)
    }

    private val shaderInstances = baseShow.shaderInstances
        .mapValues { (_, shaderInstance) ->
            MutableShaderInstance(
                findShader(shaderInstance.shaderId),
                hashMapOf(),
                shaderChannels[shaderInstance.shaderChannel],
                shaderInstance.priority
            )
        }.toMutableMap()

    init {
        // Second pass required here since they might refer to each other.
        baseShow.shaderInstances.forEach { (id, shaderInstance) ->
            val editor = findShaderInstance(id)
            val resolvedIncomingLinks = shaderInstance.incomingLinks.mapValues { (_, fromPortRef) ->
                fromPortRef.dereference(this)
            }
            editor.incomingLinks.putAll(resolvedIncomingLinks)
        }
    }

    constructor(title: String, block: MutableShow.() -> Unit = {}) : this(Show(title)) {
        this.block()
    }

    fun invoke(block: MutableShow.() -> Unit) = this.block()

    fun editLayouts(block: MutableLayouts.() -> Unit): MutableShow {
        layouts.apply(block)
        return this
    }

    fun build(showBuilder: ShowBuilder): Show {
        return Show(
            title,
            patches = patches.map { it.build(showBuilder) },
            eventBindings = eventBindings,
            controlLayout = buildControlLayout(showBuilder),
            layouts = layouts.build(showBuilder),
            shaders = showBuilder.getShaders(),
            shaderInstances = showBuilder.getShaderInstances(),
            dataSources = showBuilder.getDataSources(),
            controls = showBuilder.getControls()
        )
    }

    fun getShow() = build(ShowBuilder())

    override fun build(): Show = getShow()

    fun findControl(controlId: String): MutableControl =
        controls.getBang(controlId, "control")

    fun edit(buttonGroupControl: OpenButtonGroupControl, block: MutableButtonGroupControl.() -> Unit = {}) {
        (findControl(buttonGroupControl.id) as MutableButtonGroupControl).block()
    }

    fun findDataSource(dataSourceId: String): MutableDataSourcePort =
        dataSources.getBang(dataSourceId, "data source")

    fun findPatchHolder(openPatchHolder: OpenPatchHolder): MutablePatchHolder {
        return when (openPatchHolder) {
            is OpenShow -> this

            // Yuck.
            is OpenButtonControl -> return findControl(openPatchHolder.id) as MutableButtonControl

            else -> error("huh? $openPatchHolder isn't a show or a button?")
        }
    }

    fun findPanel(panelId: String): MutablePanel =
        layouts.panels.getBang(panelId, "panel")

    fun findPanel(panel: Panel): MutablePanel =
        layouts.panels.values.find { it.isFor(panel) } ?: error("mutable panel ${panel.title} not found")

    fun findShader(shaderId: String): MutableShader =
        shaders.getBang(shaderId, "shader")

    fun findShaderInstance(id: String): MutableShaderInstance =
        shaderInstances.getBang(id, "shader instance")

    fun commit(editHandler: EditHandler<Show, ShowState>) {
        editHandler.onEdit(this)
    }

    companion object {
        fun create(title: String): Show {
            return Show(title = title)
        }
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
        if (SparkleMotion.EXTRA_ASSERTIONS) mutableShaderInstances.assertNoDuplicates()
        this.mutableShaderInstances = mutableShaderInstances.toMutableList()
        this.surfaces = surfaces
    }

    constructor(basePatch: Patch, show: MutableShow) {
        mutableShaderInstances = basePatch.shaderInstanceIds.map { shaderInstanceId ->
            show.findShaderInstance(shaderInstanceId)
        }.toMutableList()
        if (SparkleMotion.EXTRA_ASSERTIONS) mutableShaderInstances.assertNoDuplicates()

        this.surfaces = basePatch.surfaces
    }

    constructor(block: MutablePatch.() -> Unit = {}) {
        this.mutableShaderInstances = arrayListOf()
        this.surfaces = Surfaces.AllSurfaces

        block()
    }

    fun build(showBuilder: ShowBuilder): Patch =
        Patch(
            mutableShaderInstances.map { showBuilder.idFor(it.build(showBuilder)) },
            surfaces
        )

    fun accept(visitor: MutableShowVisitor, log: VisitationLog = VisitationLog()) {
        if (log.patches.add(this)) visitor.visit(this)

        surfaces.accept(visitor, log)
        mutableShaderInstances.forEach { it.accept(visitor, log) }
    }

    /** Build a [LinkedPatch] independent of an [baaahs.show.live.OpenShow]. */
    fun openForPreview(toolchain: Toolchain, resultContentType: ContentType): LinkedPatch? {
        val showBuilder = ShowBuilder()
        build(showBuilder)

        val openShaders = CacheBuilder<String, OpenShader> { shaderId ->
            toolchain.openShader(showBuilder.getShaders().getBang(shaderId, "shader"))
        }

        val resolvedShaderInstances =
            ShaderInstanceResolver(openShaders, showBuilder.getShaderInstances(), showBuilder.getDataSources())
                .getResolvedShaderInstances()
        val openPatch = OpenPatch(resolvedShaderInstances.values.toList(), surfaces)

        val portDiagram = PatchResolver.buildPortDiagram(openPatch)
        return portDiagram.resolvePatch(ShaderChannel.Main, resultContentType, showBuilder.getDataSources())
    }

    fun addShaderInstance(mutableShaderInstance: MutableShaderInstance): MutablePatch {
        mutableShaderInstances.add(mutableShaderInstance)
        if (SparkleMotion.EXTRA_ASSERTIONS) mutableShaderInstances.assertNoDuplicates()
        return this
    }

    fun addShaderInstance(shader: Shader, block: MutableShaderInstance.() -> Unit = {}): MutableShaderInstance {
        return addShaderInstance(MutableShader(shader), block)
    }

    fun addShaderInstance(shader: MutableShader, block: MutableShaderInstance.() -> Unit = {}): MutableShaderInstance {
        val mutableShaderInstance = MutableShaderInstance(shader)
        mutableShaderInstance.block()
        mutableShaderInstances.add(mutableShaderInstance)
        if (SparkleMotion.EXTRA_ASSERTIONS) mutableShaderInstances.assertNoDuplicates()
        return mutableShaderInstance
    }

    fun findShaderInstanceFor(shader: Shader): MutableShaderInstance {
        return mutableShaderInstances.find { it.mutableShader.build() == shader }
            ?: error("No shader instance for ${shader.title}.")
    }

    fun remove(mutableShaderInstance: MutableShaderInstance) {
        mutableShaderInstances.remove(mutableShaderInstance)
    }

    fun getEditorPanel(editableManager: EditableManager<*>) =
        PatchEditorPanel(editableManager, this)
}

data class MutableShader(
    var title: String,
    /**language=glsl*/
    var src: String
) {
    constructor(shader: Shader) : this(shader.title, shader.src)

    fun build(): Shader {
        return Shader(title, src)
    }

    fun accept(visitor: MutableShowVisitor, log: VisitationLog = VisitationLog()) {
        if (log.shaders.add(this)) visitor.visit(this)
    }

    override fun toString(): String {
        return "MutableShader(title='$title', src='[${src.length} chars]')"
    }
}

data class MutableShaderInstance(
    val mutableShader: MutableShader,
    val incomingLinks: MutableMap<String, MutablePort> = hashMapOf(),
    var shaderChannel: MutableShaderChannel = ShaderChannel.Main.editor(),
    var priority: Float = 0f
) {
    val id = randomId("MutableShaderInstance")

    fun findDataSources(): List<DataSource> {
        return incomingLinks.mapNotNull { (_, from) ->
            (from as? MutableDataSourcePort)?.dataSource
        }
    }

    fun findShaderChannels(): List<MutableShaderChannel> {
        return (incomingLinks.values.map { link ->
            link as? MutableShaderChannel
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
            shaderChannel.build(),
            priority
        )
    }

    fun accept(visitor: MutableShowVisitor, log: VisitationLog = VisitationLog()) {
        if (log.shaderInstances.add(this)) visitor.visit(this)
        mutableShader.accept(visitor, log)
        shaderChannel.accept(visitor, log)
        incomingLinks.forEach { (_, port) -> port.accept(visitor, log) }
    }

    fun getEditorPanel(patchEditorPanel: PatchEditorPanel): DialogPanel =
        patchEditorPanel.ShaderInstanceEditorPanel(this)

    fun isFilter(openShader: OpenShader): Boolean = with(openShader) {
        inputPorts.any {
            it.contentType == outputPort.contentType && incomingLinks[it.id]?.let { link ->
                link is MutableShaderChannel && link.id == shaderChannel.id
            } == true
        }
    }
}


class ShowBuilder {
    private val panelIds = UniqueIds<Panel>()
    private val controlIds = UniqueIds<Control>()
    private val dataSourceIds = UniqueIds<DataSource>()
    private val shaderIds = UniqueIds<Shader>()
    private val shaderInstanceIds = UniqueIds<ShaderInstance>()

    fun idFor(panel: Panel): String {
        return panelIds.idFor(panel) { panel.suggestId() }
    }

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

    companion object {
        fun forImplicitControls(
            existingControls: Map<String, Control>,
            existingDataSources: Map<String, DataSource>
        ): ShowBuilder = ShowBuilder().apply {
            controlIds.putAll(existingControls)
            dataSourceIds.putAll(existingDataSources)
        }
    }
}

class VisitationLog {
    val patchHolders = mutableSetOf<MutablePatchHolder>()
    val patches = mutableSetOf<MutablePatch>()
    val surfaces = mutableSetOf<Surfaces>()
    val shaderInstances = mutableSetOf<MutableShaderInstance>()
    val shaders = mutableSetOf<MutableShader>()
    val shaderChannels = mutableSetOf<MutableShaderChannel>()
    val controls = mutableSetOf<MutableControl>()
    val dataSources = mutableSetOf<MutableDataSourcePort>()
}