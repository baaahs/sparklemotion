package baaahs.show

import baaahs.ShowState
import baaahs.getBang
import baaahs.glshaders.AutoWirer
import baaahs.glshaders.GlslAnalyzer
import baaahs.glshaders.LinkedPatch
import baaahs.glshaders.Plugins
import baaahs.randomId
import baaahs.show.live.OpenPatch
import baaahs.show.live.ShaderInstanceResolver
import baaahs.util.UniqueIds
import kotlinx.serialization.*

abstract class PatchHolderEditor(
    private val basePatchHolder: PatchHolder,
    dataSources: Map<String, DataSource>
) {
    abstract val displayType: String
    protected abstract val showEditor: ShowEditor
    abstract val descendents: List<PatchHolderEditor>

    var title = basePatchHolder.title

    val patchMappings by lazy {
        basePatchHolder.patches.map { PatchEditor(it, showEditor) }.toMutableList()
    }
    val eventBindings = basePatchHolder.eventBindings.toMutableList()

    private val controlLayout by lazy {
        basePatchHolder.controlLayout
            .mapValues { (_, v) ->
                v.map {
                    ControlEditor(it.dereference(dataSources))
                }.toMutableList()
            }.toMutableMap()
    }

    fun addPatch(block: PatchEditor.() -> Unit): PatchHolderEditor {
        val patchEditor = PatchEditor(emptyList(), Surfaces.AllSurfaces)
        patchEditor.block()
        patchMappings.add(patchEditor)
        return this
    }

    fun addPatch(patch: PatchEditor): PatchHolderEditor {
        patchMappings.add(patch)
        return this
    }

    fun editPatch(index: Int, block: PatchEditor.() -> Unit): PatchHolderEditor {
        patchMappings[index].block()
        return this
    }

    fun findDataSources(): Set<DataSource> =
        (
            findControlDataSources() +
                patchMappings.flatMap { it.findDataSources() } +
                descendents.flatMap { it.findDataSources() }
        ).toSet()

    fun findShaderInstances(): Set<ShaderInstanceEditor> =
        (
            patchMappings.flatMap { it.findShaderInstances() } +
                descendents.flatMap { it.findShaderInstances() }
        ).toSet()

    fun findShaderChannels(): Set<ShaderChannel> =
        showEditor.collectShaderChannels()

    protected fun collectShaderChannels(): Set<ShaderChannel> =
        (
            patchMappings.flatMap { it.findShaderChannels() } +
                descendents.flatMap { it.collectShaderChannels() }
        ).toSet()

    fun addControl(panel: String, control: Control) {
        controlLayout.getOrPut(panel) { arrayListOf() }.add(ControlEditor(control))
    }

    fun removeControl(panel: String, index: Int): ControlEditor {
        return controlLayout.getOrPut(panel) { arrayListOf() }.removeAt(index)
    }

    fun findControlDataSources(): Set<DataSource> {
        return controlLayout.values.flatMap {
            it.filterIsInstance<DataSourceEditor>().map { it.dataSource }
        }.toSet()
    }

    fun editControlLayout(panelName: String): MutableList<ControlEditor> {
        return controlLayout.getOrPut(panelName) { mutableListOf() }
    }

    internal fun buildControlLayout(showBuilder: ShowBuilder): Map<String, List<ControlRef>> {
        return controlLayout.mapValues { (_, v) ->
            v.map { it.toRef(showBuilder) }
        }
    }

    open fun isChanged(): Boolean {
        return title != basePatchHolder.title
                || patchMappings != basePatchHolder.patches
    }

    abstract fun getShow(): Show
    abstract fun getShowState(): ShowState
}

class ShowEditor(
    private val baseShow: Show, baseShowState: ShowState = ShowState.Empty
) : PatchHolderEditor(baseShow, baseShow.dataSources) {
    override val displayType: String get() = "Show"
    override val showEditor: ShowEditor get() = this
    override val descendents: List<PatchHolderEditor> get() = scenes

    internal val dataSources = baseShow.dataSources
        .mapValues { (_, shader) -> DataSourceEditor(shader) }
        .toMutableMap()

    internal val shaders = baseShow.shaders
        .mapValues { (_, shader) -> ShaderEditor(shader) }
        .toMutableMap()

    val shaderInstances = baseShow.shaderInstances
        .mapValues { (_, shaderInstance) ->
            ShaderInstanceEditor(
                findShader(shaderInstance.shaderId),
                hashMapOf(),
                shaderInstance.shaderChannel
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

    private val scenes = baseShow.scenes.map { SceneEditor(it) }.toMutableList()
    private val layoutEditor = LayoutsEditor(baseShow.layouts)

    private var selectedScene: Int = baseShowState.selectedScene
    private val patchSetSelections: MutableList<Int> = baseShowState.patchSetSelections.toMutableList()

    constructor(title: String) : this(Show(title), ShowState.Empty)

    fun invoke(block: ShowEditor.() -> Unit) = this.block()

    fun addScene(title: String, block: SceneEditor.() -> Unit): ShowEditor {
        scenes.add(SceneEditor(Scene(title)).apply(block))
        if (selectedScene == -1) selectedScene = 0
        patchSetSelections.add(1)
        return this
    }

    fun getSceneEditor(sceneIndex: Int): SceneEditor = scenes[sceneIndex]

    fun editScene(sceneIndex: Int, block: SceneEditor.() -> Unit): ShowEditor {
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

    fun editLayouts(block: LayoutsEditor.() -> Unit): ShowEditor {
        layoutEditor.apply(block)
        return this
    }

    fun build(showBuilder: ShowBuilder): Show {
        return Show(
            title,
            patches = patchMappings.map { it.build(showBuilder) },
            eventBindings = eventBindings,
            controlLayout = buildControlLayout(showBuilder),
            scenes = scenes.map { it.build(showBuilder) },
            layouts = layoutEditor.build(),
            shaders = showBuilder.getShaders(),
            shaderInstances = showBuilder.getShaderInstances(),
            dataSources = showBuilder.getDataSources()
        )
    }

    override fun getShow() = build(ShowBuilder())
    override fun getShowState() = ShowState(selectedScene, patchSetSelections)

    fun findShader(shaderId: String): ShaderEditor =
        shaders.getBang(shaderId, "shader")

    fun findShaderInstance(id: String): ShaderInstanceEditor =
        shaderInstances.getBang(id, "shader instance")


    inner class SceneEditor(baseScene: Scene) : PatchHolderEditor(baseScene, baseShow.dataSources) {
        override val displayType: String get() = "Scene"
        override val showEditor: ShowEditor get() = this@ShowEditor
        override val descendents: List<PatchHolderEditor> get() = patchSets

        private val patchSets = baseScene.patchSets.map { PatchSetEditor(it) }.toMutableList()

        private fun maybeFixPatchSetSelection() {
            val sceneIndex = scenes.indexOf(this)
            if (sceneIndex != -1 && patchSetSelections[sceneIndex] == -1) {
                patchSetSelections[sceneIndex] = 0
            }
        }

        fun insertPatchSet(patchSetEditor: PatchSetEditor, index: Int): SceneEditor {
            patchSets.add(index, patchSetEditor)
            maybeFixPatchSetSelection()
            return this
        }

        fun addPatchSet(title: String, block: PatchSetEditor.() -> Unit): SceneEditor {
            patchSets.add(PatchSetEditor(PatchSet(title)).apply(block))
            maybeFixPatchSetSelection()
            return this
        }

        fun getPatchSetEditor(index: Int): PatchSetEditor = patchSets[index]

        fun editPatchSet(index: Int, block: PatchSetEditor.() -> Unit): SceneEditor {
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

        fun removePatchSet(index: Int): SceneEditor {
            patchSets.removeAt(index)
            return this
        }

        fun build(showBuilder: ShowBuilder): Scene {
            return Scene(
                title,
                patches = patchMappings.map { it.build(showBuilder) },
                eventBindings = eventBindings,
                controlLayout = buildControlLayout(showBuilder),
                patchSets = patchSets.map { it.build(showBuilder) }
            )
        }

        override fun getShow() = this@ShowEditor.getShow()
        override fun getShowState() = this@ShowEditor.getShowState()

        inner class PatchSetEditor(basePatchSet: PatchSet) : PatchHolderEditor(
            basePatchSet,
            baseShow.dataSources
        ) {
            override val displayType: String get() = "Patch"
            override val showEditor: ShowEditor get() = this@ShowEditor
            override val descendents: List<PatchHolderEditor> get() = emptyList()

            fun build(showBuilder: ShowBuilder): PatchSet {
                return PatchSet(
                    title,
                    patches = patchMappings.map { it.build(showBuilder) },
                    eventBindings = eventBindings,
                    controlLayout = buildControlLayout(showBuilder)
                )
            }

            override fun getShow() = this@ShowEditor.getShow()
            override fun getShowState() = this@ShowEditor.getShowState()

        }
    }

    companion object {
        fun create(title: String): Show {
            return Show(title = title)
        }
    }
}

class LayoutsEditor(baseLayouts: Layouts) {
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

class PatchEditor {
    val id: String = randomId("patch-editor")

    val shaderInstances: MutableList<ShaderInstanceEditor>
    var surfaces: Surfaces
    constructor(
        shaderInstances: List<ShaderInstanceEditor> = emptyList(),
        surfaces: Surfaces = Surfaces.AllSurfaces
    ) {
        this.shaderInstances = shaderInstances.toMutableList()
        this.surfaces = surfaces
    }

    constructor(basePatch: Patch, show: ShowEditor) {
        this.shaderInstances = basePatch.shaderInstanceIds.map { shaderInstanceId ->
            show.findShaderInstance(shaderInstanceId)
        }.toMutableList()

        this.surfaces = basePatch.surfaces
    }

    constructor(block: PatchEditor.() -> Unit = {}) {
        this.shaderInstances = arrayListOf()
        this.surfaces = Surfaces.AllSurfaces

        block()
    }

    fun findDataSources(): Set<DataSource> =
        shaderInstances.flatMap { it.findDataSources() }.toSet()

    fun findShaderInstances(): Set<ShaderInstanceEditor> =
        shaderInstances.toSet()

    fun findShaderChannels(): List<ShaderChannel> {
        return findShaderInstances().flatMap { shaderInstanceEditor ->
            shaderInstanceEditor.findShaderChannels()
        }
    }

    fun build(showBuilder: ShowBuilder): Patch = Patch.from(this, showBuilder)

    /** Build a [LinkedPatch] independent of an [baaahs.OpenShow]. */
    fun openForPreview(): LinkedPatch? {
        val showBuilder = ShowBuilder()
        build(showBuilder)

        val openShaders = showBuilder.getShaders().mapValues { (_, shader) ->
            GlslAnalyzer().asShader(shader.src) }

        val resolvedShaderInstances =
            ShaderInstanceResolver(openShaders, showBuilder.getShaderInstances(), showBuilder.getDataSources())
                .getResolvedShaderInstances()
        val openPatch = OpenPatch(resolvedShaderInstances.values.toList(), surfaces)

        val autoWirer = AutoWirer(Plugins.findAll())
        val merge = autoWirer.merge(mapOf(surfaces to listOf(openPatch)))
        return merge[surfaces]
    }

    fun addShaderInstance(shaderInstanceEditor: ShaderInstanceEditor): PatchEditor {
        shaderInstances.add(shaderInstanceEditor)
        return this
    }

    fun addShaderInstance(shader: Shader, block: ShaderInstanceEditor.() -> Unit): PatchEditor {
        val shaderInstanceEditor = ShaderInstanceEditor(ShaderEditor(shader), hashMapOf())
        shaderInstanceEditor.block()
        shaderInstances.add(shaderInstanceEditor)
        return this
    }

    fun findShaderInstanceFor(shader: Shader): ShaderInstanceEditor {
        return shaderInstances.find { it.shader.shader == shader }
            ?: error("No shader instance for ${shader.title}.")
    }
}

data class LinkEditor(
    var from: Port?,
    var to: Port?
) {
    constructor(from: PortRef, to: PortRef, show: ShowEditor) :
            this(from.dereference(show), to.dereference(show))

    interface Port {
        fun toRef(showBuilder: ShowBuilder): PortRef
        fun displayName(): String

        infix fun linkTo(other: Port): LinkEditor = LinkEditor(this, other)
    }
}

fun DataSource.editor() = DataSourceEditor(this)

data class ShaderChannelEditor(val shaderChannel: ShaderChannel) : LinkEditor.Port {
    override fun toRef(showBuilder: ShowBuilder): PortRef =
        ShaderChannelRef(shaderChannel)

    override fun displayName(): String =
        "channel(${shaderChannel.id})"
}

data class DataSourceEditor(val dataSource: DataSource) : LinkEditor.Port {
    override fun toRef(showBuilder: ShowBuilder): PortRef =
        DataSourceRef(showBuilder.idFor(dataSource))

    override fun displayName(): String = dataSource.dataSourceName
}

data class ControlEditor(val control: Control) {
    fun toRef(showBuilder: ShowBuilder): ControlRef = control.toControlRef(showBuilder)
}

data class ShaderEditor(var shader: Shader) {
    val title: String get() = shader.title
}

@Serializable(with = ShaderChannel.ShaderChannelSerializer::class)
data class ShaderChannel(val id: String) {
    companion object {
        val Main = ShaderChannel("main")
    }

    class ShaderChannelSerializer : KSerializer<ShaderChannel> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveDescriptor("id", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): ShaderChannel {
            return ShaderChannel(decoder.decodeString())
        }

        override fun serialize(encoder: Encoder, value: ShaderChannel) {
            encoder.encodeString(value.id)
        }
    }
}

data class ShaderInstanceEditor(
    val shader: ShaderEditor,
    val incomingLinks: MutableMap<String, LinkEditor.Port> = hashMapOf(),
    var shaderChannel: ShaderChannel? = null
) {
    fun findDataSources(): List<DataSource> {
        return incomingLinks.mapNotNull { (_, from) ->
            (from as? DataSourceEditor)?.dataSource
        }
    }

    fun findShaderChannels(): List<ShaderChannel> {
        return incomingLinks.values.mapNotNull { link ->
            if (link is ShaderChannelEditor) link.shaderChannel else null
        }
    }

    fun link(portId: String, toPort: DataSource) {
        incomingLinks[portId] = toPort.editor()
    }

    fun link(portId: String, toPort: LinkEditor.Port) {
        incomingLinks[portId] = toPort
    }

    fun build(showBuilder: ShowBuilder): ShaderInstance {
        return ShaderInstance(
            showBuilder.idFor(shader.shader),
            incomingLinks.mapValues { (_, portRef) ->
                portRef.toRef(showBuilder)
            },
            shaderChannel
        )
    }
}

data class ShaderOutPortEditor(var shaderInstance: ShaderInstanceEditor, var portId: String) : LinkEditor.Port {
    override fun toRef(showBuilder: ShowBuilder): PortRef =
        ShaderOutPortRef(showBuilder.idFor(shaderInstance.build(showBuilder)), portId)

    override fun displayName(): String = "Shader \"${shaderInstance.shader.title}\" port \"$portId\""

    override fun toString(): String = "ShaderOutPortEditor(shader=${shaderInstance.shader.title} port=$portId)"
}

data class OutputPortEditor(private val portId: String) : LinkEditor.Port {
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