package baaahs.show.mutable

import baaahs.Gadget
import baaahs.ShowState
import baaahs.gl.patch.AutoWirer
import baaahs.gl.patch.ContentType
import baaahs.gl.patch.LinkedPatch
import baaahs.randomId
import baaahs.show.*
import baaahs.show.live.*
import baaahs.util.CacheBuilder
import baaahs.util.UniqueIds
import kotlinx.serialization.Transient

interface EditHandler {
    fun onShowEdit(mutableShow: MutableShow, pushToUndoStack: Boolean = true)
    fun onShowEdit(show: Show, showState: ShowState, pushToUndoStack: Boolean = true)
}

class EditContext {
    private val mutableControls = CacheBuilder<Control, MutableControl> { it.toMutable(this) }
    private val mutableShaders = CacheBuilder<Shader, MutableShader> { MutableShader(it) }
    private val mutableShaderInstances = CacheBuilder<ShaderInstance, MutableShaderInstance> {
        MutableShaderInstance(
            mutableShaders[it.shader],
            it.incomingLinks.entries.associate { (portId, sourcePort) ->
                portId to sourcePort.toMutable(this)
            }.toMutableMap(),
            it.shaderChannel,
            it.priority
        )
    }
    private val mutablePatches = CacheBuilder<Patch, MutablePatch> { MutablePatch(it, this) }

    fun getControl(control: Control) = mutableControls[control]
    fun getShader(shader: Shader) = mutableShaders[shader]
    fun getShaderInstance(shaderInstance: ShaderInstance) = mutableShaderInstances[shaderInstance]
    fun getPatch(patch: Patch) = mutablePatches[patch]

//    val allControls: List<MutableControl>
//    val allDataSources: List<MutableDataSource>
}

abstract class MutablePatchHolder(
    private val basePatchHolder: PatchHolder,
    protected val editContext: EditContext
) {
    abstract val displayType: String
    protected abstract val mutableShow: MutableShow
    abstract val descendents: List<MutablePatchHolder>

    var title = basePatchHolder.title

    val patches by lazy {
        basePatchHolder.patches.map { editContext.getPatch(it) }.toMutableList()
    }

    val eventBindings = basePatchHolder.eventBindings.toMutableList()

    private val controlLayout by lazy {
        basePatchHolder.controlLayout
            .mapValues { (_, v) ->
                v.map { it.toMutable(editContext) }.toMutableList()
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

    fun findDataSources(): Set<DataSource> =
        (
            findControlDataSources() +
                patches.flatMap { it.findDataSources() } +
                descendents.flatMap { it.findDataSources() }
        ).toSet()

    fun findShaderInstances(): Set<MutableShaderInstance> =
        (
            patches.flatMap { it.findShaderInstances() } +
                descendents.flatMap { it.findShaderInstances() }
        ).toSet()

    fun findShaderChannels(): Set<ShaderChannel> =
        mutableShow.collectShaderChannels()

    protected fun collectShaderChannels(): Set<ShaderChannel> =
        (
            patches.flatMap { it.findShaderChannels() } +
                descendents.flatMap { it.collectShaderChannels() }
        ).toSet()

    fun addControl(panel: String, control: MutableControl) {
        controlLayout.getOrPut(panel) { arrayListOf() }.add(control)
    }

    fun removeControl(panel: String, index: Int): MutableControl {
        return controlLayout.getOrPut(panel) { arrayListOf() }.removeAt(index)
    }

    fun findControlDataSources(): Set<DataSource> {
        return controlLayout.values.flatMap {
            it.filterIsInstance<MutableGadgetControl>().map { it.controlledDataSource }
        }.toSet()
    }

    fun editControlLayout(panelName: String): MutableList<MutableControl> {
        return controlLayout.getOrPut(panelName) { mutableListOf() }
    }

    internal fun buildControlLayout(): Map<String, List<Control>> {
        return controlLayout.mapValues { (_, v) ->
            v.map { it.build() }
        }
    }

    open fun isChanged(): Boolean {
        return title != basePatchHolder.title
                || patches != basePatchHolder.patches
                || eventBindings != basePatchHolder.eventBindings
                || controlLayout != basePatchHolder.controlLayout
    }

    abstract fun getShow(): Show
    abstract fun getShowState(): ShowState
}

class MutableShow(
    baseShow: Show,
    baseShowState: ShowState = ShowState.Empty
) : MutablePatchHolder(baseShow, EditContext()) {
    override val displayType: String get() = "Show"
    override val mutableShow: MutableShow get() = this
    override val descendents: List<MutablePatchHolder> get() = scenes

    private val scenes = baseShow.scenes.map { MutableScene(it) }.toMutableList()
    private val mutableLayouts = MutableLayouts(baseShow.layouts)

    private var selectedScene: Int = baseShowState.selectedScene
    private val patchSetSelections: MutableList<Int> = baseShowState.patchSetSelections.toMutableList()

    constructor(title: String) : this(Show(title), ShowState.Empty)

    fun invoke(block: MutableShow.() -> Unit) = this.block()

    fun addScene(title: String, block: MutableScene.() -> Unit): MutableShow {
        scenes.add(MutableScene(Scene(title)).apply(block))
        if (selectedScene == -1) selectedScene = 0
        patchSetSelections.add(1)
        return this
    }

    fun getMutableScene(sceneIndex: Int): MutableScene = scenes[sceneIndex]

    fun editScene(sceneIndex: Int, block: MutableScene.() -> Unit): MutableShow {
        scenes[sceneIndex].apply(block)
        return this
    }

    fun moveScene(fromIndex: Int, toIndex: Int) {
        scenes.add(toIndex, scenes.removeAt(fromIndex))
        if (selectedScene == fromIndex) {
            selectedScene = toIndex
        } else if (selectedScene == toIndex) {
            selectedScene = fromIndex
        }
        patchSetSelections.add(toIndex, patchSetSelections.removeAt(fromIndex))
    }

    fun editLayouts(block: MutableLayouts.() -> Unit): MutableShow {
        mutableLayouts.apply(block)
        return this
    }

    fun build(buildContext: BuildContext) =
        Show(
            title,
            patches = patches.map { it.build(buildContext) },
            eventBindings = eventBindings,
            controlLayout = buildControlLayout(),
            scenes = scenes.map { it.build(buildContext) },
            layouts = mutableLayouts.build()
        ).also { IdGenerator().visitShow(it) }

    override fun getShow() = build(BuildContext())
    override fun getShowState() = ShowState(selectedScene, patchSetSelections)

    inner class MutableScene(baseScene: Scene) : MutablePatchHolder(
        baseScene,
        EditContext()
    ) {
        override val displayType: String get() = "Scene"
        override val mutableShow: MutableShow get() = this@MutableShow
        override val descendents: List<MutablePatchHolder> get() = patchSets

        private val patchSets = baseScene.patchSets.map { MutablePatchSet(it) }.toMutableList()

        private fun maybeFixPatchSetSelection() {
            val sceneIndex = scenes.indexOf(this)
            if (sceneIndex != -1 && patchSetSelections[sceneIndex] == -1) {
                patchSetSelections[sceneIndex] = 0
            }
        }

        fun insertPatchSet(patchSetEditor: MutablePatchSet, index: Int): MutableScene {
            patchSets.add(index, patchSetEditor)
            maybeFixPatchSetSelection()
            return this
        }

        fun addPatchSet(title: String, block: MutablePatchSet.() -> Unit): MutableScene {
            patchSets.add(MutablePatchSet(PatchSet(title)).apply(block))
            maybeFixPatchSetSelection()
            return this
        }

        fun getMutablePatchSet(index: Int): MutablePatchSet = patchSets[index]

        fun editPatchSet(index: Int, block: MutablePatchSet.() -> Unit): MutableScene {
            patchSets[index].block()
            return this
        }

        fun movePatchSet(fromIndex: Int, toIndex: Int) {
            patchSets.add(toIndex, patchSets.removeAt(fromIndex))
            val mySceneIndex = scenes.indexOf(this)
            val previousSelection = patchSetSelections[mySceneIndex]
            if (previousSelection == fromIndex) {
                patchSetSelections[mySceneIndex] = toIndex
            } else if (previousSelection == toIndex) {
                patchSetSelections[mySceneIndex] = fromIndex
            }
        }

        fun removePatchSet(index: Int): MutableScene {
            patchSets.removeAt(index)
            return this
        }

        fun build(buildContext: BuildContext) =
            Scene(
                title,
                patches = patches.map { it.build(buildContext) },
                eventBindings = eventBindings,
                controlLayout = buildControlLayout(),
                patchSets = patchSets.map { it.build(buildContext) }
            )

        override fun getShow() = this@MutableShow.getShow()
        override fun getShowState() = this@MutableShow.getShowState()

        inner class MutablePatchSet(basePatchSet: PatchSet) : MutablePatchHolder(
            basePatchSet,
            EditContext()
        ) {
            override val displayType: String get() = "Patch"
            override val mutableShow: MutableShow get() = this@MutableShow
            override val descendents: List<MutablePatchHolder> get() = emptyList()

            fun build(buildContext: BuildContext) =
                PatchSet(
                    title,
                    patches = patches.map { it.build(buildContext) },
                    eventBindings = eventBindings,
                    controlLayout = buildControlLayout()
                )

            override fun getShow() = this@MutableShow.getShow()
            override fun getShowState() = this@MutableShow.getShowState()

        }
    }

    companion object {
        fun create(title: String): Show {
            return Show(title = title)
        }
    }
}

class IdGenerator: ShowVisitor() {
    private val controls = UniqueIds<Control>()
    private val dataSources = UniqueIds<DataSource>()
    private val shaders = UniqueIds<Shader>()
    private val shaderInstances = UniqueIds<ShaderInstance>()

    override fun visitShow(show: Show) {
        super.visitShow(show)
    }

    override fun visitControl(control: Control, depth: Int) {
        control.setId(controls.idFor(control))
        super.visitControl(control, depth)
    }

    override fun visitDataSource(dataSource: DataSource) {
        dataSource.setId(dataSources.idFor(dataSource))
        super.visitDataSource(dataSource)
    }

    override fun visitShader(shader: Shader) {
        shader.setId(shaders.idFor(shader))
        super.visitShader(shader)
    }

    override fun visitShaderInstance(shaderInstance: ShaderInstance) {
        shaderInstance.setId(shaderInstances.idFor(shaderInstance))
        super.visitShaderInstance(shaderInstance)
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

    constructor(basePatch: Patch, editContext: EditContext) {
        this.mutableShaderInstances = basePatch.shaderInstances.map { shaderInstance ->
            editContext.getShaderInstance(shaderInstance)
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

    fun build(buildContext: BuildContext): Patch {
        return Patch(
            mutableShaderInstances.map { it.build(buildContext) },
            surfaces
        )
    }

    /** Build a [LinkedPatch] independent of an [baaahs.show.live.OpenShow]. */
    fun openForPreview(autoWirer: AutoWirer): LinkedPatch? {
        val buildContext = BuildContext()
        build(buildContext)

        val openShaders = buildContext.getShaders().mapValues { (_, shader) ->
            autoWirer.glslAnalyzer.openShader(shader)
        }

        val patch = Patch(buildContext.getShaderInstances().values.toList(), surfaces)
        IdGenerator().visitShow(Show("preview", patches = listOf(patch)))

        val portDiagram = autoWirer.buildPortDiagram(ShaderLookup(openShaders), patch)
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
}

sealed class MutableSourcePort {
    abstract fun build(buildContext: BuildContext): SourcePort
    abstract fun displayName(): String
}

data class MutableDataSourceSourcePort(var dataSource: DataSource) : MutableSourcePort() {
    override fun build(buildContext: BuildContext): SourcePort = DataSourceSourcePort(dataSource)
    override fun displayName(): String = dataSource.dataSourceName
}

data class MutableShaderOutSourcePort(var mutableShaderInstance: MutableShaderInstance): MutableSourcePort() {
    override fun build(buildContext: BuildContext): SourcePort = ShaderOutSourcePort(buildContext.build(mutableShaderInstance))
    override fun displayName(): String = "Shader \"${mutableShaderInstance.mutableShader.title} output"
}

data class MutableShaderChannelSourcePort(var shaderChannel: ShaderChannel) : MutableSourcePort() {
    override fun build(buildContext: BuildContext): SourcePort = ShaderChannelSourcePort(shaderChannel)
    override fun displayName(): String = "channel(${shaderChannel.id})"
}

data class MutableConstSourcePort(var glsl: String) : MutableSourcePort() {
    override fun build(buildContext: BuildContext): SourcePort = ConstSourcePort(glsl)
    override fun displayName(): String = "const($glsl)"
}

data class MutableNoOpSourcePort(@Transient val `_`: Boolean = false) : MutableSourcePort() {
    override fun build(buildContext: BuildContext): SourcePort = NoOpSourcePort()
    override fun displayName(): String = "Nothing"
}

interface MutableControl {
    fun build(): Control
}

data class MutableButtonGroupControl(
    var title: String
) : MutableControl {
    override fun build() = ButtonGroupControl(title)
}

data class MutableGadgetControl(
    var gadget: Gadget,
    val controlledDataSource: DataSource
) : MutableControl {
    override fun build() = GadgetControl(gadget, controlledDataSource)

    fun open(): OpenControl {
        return OpenGadgetControl(gadget, controlledDataSource)
    }
}

data class MutableShader(
    var title: String,
    var type: ShaderType,
    /**language=glsl*/
    var src: String
) {
    constructor(shader: Shader) : this(shader.title, shader.type, shader.src)

    fun build() = Shader(title, type, src)
}

data class MutableShaderInstance(
    val mutableShader: MutableShader,
    val incomingLinks: MutableMap<String, MutableSourcePort> = hashMapOf(),
    var shaderChannel: ShaderChannel = ShaderChannel.Main,
    var priority: Float = 0f
) {
    val id = randomId("MutableShaderInstance")

    fun findDataSources(): List<DataSource> {
        return incomingLinks.mapNotNull { (_, from) ->
            (from as? MutableDataSourceSourcePort)?.dataSource
        }
    }

    fun findShaderChannels(): List<ShaderChannel> {
        return listOf(shaderChannel) +
                incomingLinks.values
                    .filterIsInstance<MutableShaderChannelSourcePort>()
                    .map { it.shaderChannel }
    }

    fun link(portId: String, toDataSource: DataSource) {
        incomingLinks[portId] = MutableDataSourceSourcePort(toDataSource)
    }

    fun link(portId: String, toSourcePort: MutableSourcePort) {
        incomingLinks[portId] = toSourcePort
    }

    fun build(buildContext: BuildContext): ShaderInstance {
        return ShaderInstance(
            mutableShader.build(),
            incomingLinks.mapValues { (_, mutablePort) ->
                mutablePort.build(buildContext)
            },
            shaderChannel,
            priority
        )
    }

    companion object {
        val defaultOrder = compareBy<MutableShaderInstance>(
            { it.mutableShader.type.priority },
            { it.mutableShader.title }
        )
    }
}

class BuildContext {
    private val controlIds = UniqueIds<Control>()
    private val dataSourceIds = UniqueIds<DataSource>()
    private val shaderIds = UniqueIds<Shader>()
    private val shaderInstanceIds = UniqueIds<ShaderInstance>()

    private val shaderInstances = CacheBuilder<MutableShaderInstance, ShaderInstance> {
        ShaderInstance(
            it.mutableShader.build(),
            it.incomingLinks.mapValues { (_, sourcePort) -> sourcePort.build(this) },
            it.shaderChannel,
            it.priority
        ).also { built -> built.setId(shaderInstanceIds.idFor(built)) }
    }

    fun build(mutableShaderInstance: MutableShaderInstance): ShaderInstance =
        shaderInstances[mutableShaderInstance]

    fun getControls(): Map<String, Control> = controlIds.all()
    fun getDataSources(): Map<String, DataSource> = dataSourceIds.all()
    fun getShaders(): Map<String, Shader> = shaderIds.all()
    fun getShaderInstances(): Map<String, ShaderInstance> =
        shaderInstances.all.values.associateBy { it.id }
}