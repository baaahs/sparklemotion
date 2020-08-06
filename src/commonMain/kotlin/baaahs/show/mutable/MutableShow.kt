package baaahs.show.mutable

import baaahs.ShowState
import baaahs.getBang
import baaahs.glshaders.AutoWirer
import baaahs.glshaders.LinkedPatch
import baaahs.randomId
import baaahs.show.*
import baaahs.show.live.OpenPatch
import baaahs.show.live.ShaderInstanceResolver
import baaahs.util.UniqueIds

abstract class MutablePatchHolder(
    private val basePatchHolder: PatchHolder,
    dataSources: Map<String, DataSource>
) {
    abstract val displayType: String
    protected abstract val mutableShow: MutableShow
    abstract val descendents: List<MutablePatchHolder>

    var title = basePatchHolder.title

    val patches by lazy {
        basePatchHolder.patches.map { MutablePatch(it, mutableShow) }.toMutableList()
    }
    val eventBindings = basePatchHolder.eventBindings.toMutableList()

    private val controlLayout by lazy {
        basePatchHolder.controlLayout
            .mapValues { (_, v) ->
                v.map {
                    MutableControl(it.dereference(dataSources))
                }.toMutableList()
            }.toMutableMap()
    }

    fun addPatch(block: MutablePatch.() -> Unit): MutablePatchHolder {
        val mutablePatch = MutablePatch(
            emptyList(),
            Surfaces.AllSurfaces
        )
        mutablePatch.block()
        patches.add(mutablePatch)
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

    fun addControl(panel: String, control: Control) {
        controlLayout.getOrPut(panel) { arrayListOf() }.add(MutableControl(control))
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

    internal fun buildControlLayout(showBuilder: ShowBuilder): Map<String, List<ControlRef>> {
        return controlLayout.mapValues { (_, v) ->
            v.map { it.toRef(showBuilder) }
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
    private val baseShow: Show, baseShowState: ShowState = ShowState.Empty
) : MutablePatchHolder(baseShow, baseShow.dataSources) {
    override val displayType: String get() = "Show"
    override val mutableShow: MutableShow get() = this
    override val descendents: List<MutablePatchHolder> get() = scenes

    internal val dataSources = baseShow.dataSources
        .mapValues { (_, shader) -> MutableDataSource(shader) }
        .toMutableMap()

    internal val shaders = baseShow.shaders
        .mapValues { (_, shader) -> MutableShader(shader) }
        .toMutableMap()

    val shaderInstances = baseShow.shaderInstances
        .mapValues { (_, shaderInstance) ->
            MutableShaderInstance(
                findShader(shaderInstance.shaderId),
                hashMapOf(),
                shaderInstance.shaderChannel,
                shaderInstance.priority
            )
        }.toMutableMap()
    init {
        // Second pass required here since they might refer to each other.
        baseShow.shaderInstances.values.zip(shaderInstances.values).forEach { (shaderInstance, editor) ->
            editor.incomingLinks.putAll(
                shaderInstance.incomingLinks.mapValues { (_, fromPortRef) ->
                    fromPortRef.dereference(this)
                }
            )
        }
    }

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

    fun build(showBuilder: ShowBuilder): Show {
        return Show(
            title,
            patches = patches.map { it.build(showBuilder) },
            eventBindings = eventBindings,
            controlLayout = buildControlLayout(showBuilder),
            scenes = scenes.map { it.build(showBuilder) },
            layouts = mutableLayouts.build(),
            shaders = showBuilder.getShaders(),
            shaderInstances = showBuilder.getShaderInstances(),
            dataSources = showBuilder.getDataSources()
        )
    }

    override fun getShow() = build(ShowBuilder())
    override fun getShowState() = ShowState(selectedScene, patchSetSelections)

    fun findShader(shaderId: String): MutableShader =
        shaders.getBang(shaderId, "shader")

    fun findShaderInstance(id: String): MutableShaderInstance =
        shaderInstances.getBang(id, "shader instance")


    inner class MutableScene(baseScene: Scene) : MutablePatchHolder(baseScene, baseShow.dataSources) {
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

        fun build(showBuilder: ShowBuilder): Scene {
            return Scene(
                title,
                patches = patches.map { it.build(showBuilder) },
                eventBindings = eventBindings,
                controlLayout = buildControlLayout(showBuilder),
                patchSets = patchSets.map { it.build(showBuilder) }
            )
        }

        override fun getShow() = this@MutableShow.getShow()
        override fun getShowState() = this@MutableShow.getShowState()

        inner class MutablePatchSet(basePatchSet: PatchSet) : MutablePatchHolder(
            basePatchSet,
            baseShow.dataSources
        ) {
            override val displayType: String get() = "Patch"
            override val mutableShow: MutableShow get() = this@MutableShow
            override val descendents: List<MutablePatchHolder> get() = emptyList()

            fun build(showBuilder: ShowBuilder): PatchSet {
                return PatchSet(
                    title,
                    patches = patches.map { it.build(showBuilder) },
                    eventBindings = eventBindings,
                    controlLayout = buildControlLayout(showBuilder)
                )
            }

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
        Patch.from(this, showBuilder)

    /** Build a [LinkedPatch] independent of an [baaahs.OpenShow]. */
    fun openForPreview(autoWirer: AutoWirer, shaderType: ShaderType): LinkedPatch? {
        val showBuilder = ShowBuilder()
        build(showBuilder)

        val openShaders = showBuilder.getShaders().mapValues { (_, shader) ->
            autoWirer.glslAnalyzer.openShader(shader.src) }

        val resolvedShaderInstances =
            ShaderInstanceResolver(openShaders, showBuilder.getShaderInstances(), showBuilder.getDataSources())
                .getResolvedShaderInstances()
        val openPatch = OpenPatch(resolvedShaderInstances.values.toList(), surfaces)

        val portDiagram = autoWirer.buildPortDiagram(openPatch)
        return portDiagram.resolvePatch(ShaderChannel.Main, shaderType.resultContentType)
    }

    fun addShaderInstance(mutableShaderInstance: MutableShaderInstance): MutablePatch {
        mutableShaderInstances.add(mutableShaderInstance)
        return this
    }

    fun addShaderInstance(shader: Shader, block: MutableShaderInstance.() -> Unit): MutablePatch {
        val mutableShaderInstance = MutableShaderInstance(
            MutableShader(shader), hashMapOf(), null, 0f
        )
        mutableShaderInstance.block()
        mutableShaderInstances.add(mutableShaderInstance)
        return this
    }

    fun findShaderInstanceFor(shader: Shader): MutableShaderInstance {
        return mutableShaderInstances.find { it.mutableShader.build() == shader }
            ?: error("No shader instance for ${shader.title}.")
    }
}

data class MutableLink(
    var from: Port?,
    var to: Port?
) {
    constructor(from: PortRef, to: PortRef, show: MutableShow) :
            this(from.dereference(show), to.dereference(show))

    interface Port {
        fun toRef(showBuilder: ShowBuilder): PortRef
        fun displayName(): String

        infix fun linkTo(other: Port): MutableLink =
            MutableLink(this, other)
    }
}

fun DataSource.editor() = MutableDataSource(this)

data class MutableShaderChannel(val shaderChannel: ShaderChannel) :
    MutableLink.Port {
    override fun toRef(showBuilder: ShowBuilder): PortRef =
        ShaderChannelRef(shaderChannel)

    override fun displayName(): String =
        "channel(${shaderChannel.id})"
}

data class MutableDataSource(val dataSource: DataSource) :
    MutableLink.Port {
    override fun toRef(showBuilder: ShowBuilder): PortRef =
        DataSourceRef(showBuilder.idFor(dataSource))

    override fun displayName(): String = dataSource.dataSourceName
}

data class MutableControl(val control: Control) {
    fun toRef(showBuilder: ShowBuilder): ControlRef = control.toControlRef(showBuilder)
}

data class MutableShader(
    var title: String,
    var type: ShaderType,
    /**language=glsl*/
    var src: String
) {
    constructor(shader: Shader): this(shader.title, shader.type, shader.src)

    fun build(): Shader {
        return Shader(title, type, src)
    }
}

data class MutableShaderInstance(
    val mutableShader: MutableShader,
    val incomingLinks: MutableMap<String, MutableLink.Port> = hashMapOf(),
    var shaderChannel: ShaderChannel? = null,
    var priority: Float = 0f
) {
    fun findDataSources(): List<DataSource> {
        return incomingLinks.mapNotNull { (_, from) ->
            (from as? MutableDataSource)?.dataSource
        }
    }

    fun findShaderChannels(): List<ShaderChannel> {
        return incomingLinks.values.mapNotNull { link ->
            if (link is MutableShaderChannel) link.shaderChannel else null
        }
    }

    fun link(portId: String, toPort: DataSource) {
        incomingLinks[portId] = toPort.editor()
    }

    fun link(portId: String, toPort: MutableLink.Port) {
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
}

data class MutableShaderOutPort(var mutableShaderInstance: MutableShaderInstance, var portId: String) :
    MutableLink.Port {
    override fun toRef(showBuilder: ShowBuilder): PortRef =
        ShaderOutPortRef(showBuilder.idFor(mutableShaderInstance.build(showBuilder)), portId)

    override fun displayName(): String = "Shader \"${mutableShaderInstance.mutableShader.title}\" port \"$portId\""

    override fun toString(): String = "ShaderOutPortEditor(shader=${mutableShaderInstance.mutableShader.title} port=$portId)"
}

data class MutableOutputPort(private val portId: String) : MutableLink.Port {
    override fun toRef(showBuilder: ShowBuilder): PortRef =
        OutputPortRef(portId)

    override fun displayName(): String = "$portId Output"
}

class ShowBuilder {
    private val dataSourceIds = UniqueIds<DataSource>()
    private val shaderIds = UniqueIds<Shader>()
    private val shaderInstanceIds = UniqueIds<ShaderInstance>()

    fun idFor(dataSource: DataSource): String {
        return dataSourceIds.idFor(dataSource) { dataSource.suggestId() }
    }

    fun idFor(shader: Shader): String {
        return shaderIds.idFor(shader) { shader.suggestId() }
    }

    fun idFor(shaderInstance: ShaderInstance): String {
        return shaderInstanceIds.idFor(shaderInstance) { "${shaderInstance.shaderId}-inst" }
    }

    fun getDataSources(): Map<String, DataSource> = dataSourceIds.all()
    fun getShaders(): Map<String, Shader> = shaderIds.all()
    fun getShaderInstances(): Map<String, ShaderInstance> = shaderInstanceIds.all()
}