package baaahs.show

import baaahs.ShowState
import baaahs.getBang
import baaahs.glshaders.GlslAnalyzer
import baaahs.glshaders.OpenPatch

open class ControllablesEditor(
    controllables: Controllables,
    dataSources: Map<String, DataSource>
) {
    val eventBindings = controllables.eventBindings.toMutableList()
    val controlLayout = controllables.controlLayout
        .mapValues { (_, v) ->
            v.map {
                DataSourceEditor(dataSources.getBang(it.dataSourceId, "datasource"))
            }.toMutableList()
        }.toMutableMap()

    fun addControl(panel: String, dataSource: DataSource) {
        controlLayout.getOrPut(panel) { arrayListOf() }.add(DataSourceEditor(dataSource))
    }


    fun findControlDataSources(): Set<DataSource> {
        return controlLayout.values.flatMap { it.map { it.dataSource } }.toSet()
    }

    internal fun buildControlLayout(showBuilder: ShowBuilder): Map<String, List<DataSourceRef>> {
        return controlLayout.mapValues { (_, v) ->
            v.map { DataSourceRef(showBuilder.idFor(it.dataSource)) }
        }
    }
}

class ShowEditor(
    private val baseShow: Show, baseShowState: ShowState = ShowState.Empty
) : ControllablesEditor(baseShow, baseShow.dataSources) {
    var title: String = baseShow.title

    val shaders = baseShow.shaders
        .mapValues { (_, shader) -> ShaderEditor(shader) }
        .toMutableMap()
    val dataSources = baseShow.dataSources
        .mapValues { (_, shader) -> DataSourceEditor(shader) }
        .toMutableMap()

    private val scenes = baseShow.scenes.map { SceneEditor(it) }.toMutableList()
    var layouts = baseShow.layouts

    private var selectedScene: Int = baseShowState.selectedScene
    private val patchSetSelections: MutableList<Int> = baseShowState.patchSetSelections.toMutableList()

    constructor(title: String) : this(Show(title), ShowState.Empty)

    fun invoke(block: ShowEditor.() -> Unit) = this.block()

    fun addScene(title: String, block: SceneEditor.() -> Unit): ShowEditor {
        scenes.add(SceneEditor(Scene(title)).apply(block))
        patchSetSelections.add(0)
        return this
    }

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

    fun build(showBuilder: ShowBuilder): Show {
        return Show(
            title,
            scenes.map { it.build(showBuilder) },
            eventBindings,
            layouts,
            buildControlLayout(showBuilder),
            findShaders().associateBy { showBuilder.idFor(it) },
            findDataSources().associateBy { showBuilder.idFor(it) }
        )
    }

    private fun findDataSources(): Set<DataSource> =
        (findControlDataSources() + scenes.flatMap { it.findDataSources() }).toSet()

    private fun findShaders(): Set<Shader> =
        scenes.flatMap { it.findShaders() }.toSet()

    fun getShow() = build(ShowBuilder())
    fun getShowState() = ShowState(selectedScene, patchSetSelections)

    inner class SceneEditor(baseScene: Scene) : ControllablesEditor(baseScene, baseShow.dataSources) {
        var title = baseScene.title
        private val patchSets = baseScene.patchSets.map { PatchSetEditor(it) }.toMutableList()

        fun addPatchSet(title: String, block: PatchSetEditor.() -> Unit): SceneEditor {
            patchSets.add(PatchSetEditor(PatchSet(title)).apply(block))
            return this
        }

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

        fun findDataSources(): Set<DataSource> =
            (findControlDataSources() + patchSets.flatMap { it.findDataSources() }).toSet()

        fun findShaders(): Set<Shader> =
            patchSets.flatMap { it.findShaders() }.toSet()

        fun build(showBuilder: ShowBuilder): Scene {
            return Scene(
                title,
                patchSets.map { it.build(showBuilder) },
                eventBindings,
                buildControlLayout(showBuilder)
            )
        }

        fun getShow() = this@ShowEditor.getShow()
        fun getShowState() = this@ShowEditor.getShowState()

        inner class PatchSetEditor(private val basePatchSet: PatchSet) : ControllablesEditor(
            basePatchSet,
            baseShow.dataSources
        ) {
            var title = basePatchSet.title
            val patchMappings =
                basePatchSet.patches.map { PatchEditor(it, this@ShowEditor) }.toMutableList()

            fun addPatch(block: PatchEditor.() -> Unit): PatchSetEditor {
                val patchEditor = PatchEditor(emptyList(), Surfaces.AllSurfaces)
                patchEditor.block()
                patchMappings.add(patchEditor)
                return this
            }

            fun addPatch(patch: PatchEditor): PatchSetEditor {
                patchMappings.add(patch)
                return this
            }

            fun editPatch(index: Int, block: PatchEditor.() -> Unit) {
                TODO()
            }

            fun isChanged(): Boolean {
                return title != basePatchSet.title
                        || patchMappings != basePatchSet.patches
            }

            fun findDataSources(): Set<DataSource> =
                (findControlDataSources() + patchMappings.flatMap { it.findDataSources() }).toSet()

            fun findShaders(): Set<Shader> =
                patchMappings.flatMap { it.findShaders() }.toSet()

            fun build(showBuilder: ShowBuilder): PatchSet {
                return PatchSet(
                    title,
                    patchMappings.map { it.build(showBuilder) },
                    eventBindings,
                    buildControlLayout(showBuilder)
                )
            }

            fun getShowEditor() = this@ShowEditor
            fun getShow() = this@ShowEditor.getShow()
            fun getShowState() = this@ShowEditor.getShowState()

        }
    }

    companion object {
        fun create(title: String): Show {
            return Show(title = title)
        }
    }
}

class PatchEditor {
    var surfaces: Surfaces

    var links: MutableList<LinkEditor>

    constructor(links: List<LinkEditor> = listOf(), surfaces: Surfaces = Surfaces.AllSurfaces) {
        this.links = links.toMutableList()
        this.surfaces = surfaces
    }

    constructor(basePatch: Patch, show: ShowEditor) {
        this.links = basePatch.links.map { link -> LinkEditor(link.from, link.to, show) }.toMutableList()
        this.surfaces = basePatch.surfaces
    }

    fun findDataSources(): Set<DataSource> =
        links.mapNotNull { (it.from as? DataSourceEditor)?.dataSource }.toSet()

    fun findShaders(): Set<Shader> =
        links.mapNotNull { (it.from as? ShaderPortEditor)?.shader }.toSet() +
                links.mapNotNull { (it.to as? ShaderPortEditor)?.shader }.toSet()

    fun build(showBuilder: ShowBuilder): Patch {
        return Patch(links.map { it.toRef(showBuilder) }, surfaces)
    }

    fun open(): OpenPatch {
        val showBuilder = ShowBuilder()
        return OpenPatch(
            build(showBuilder),
            showBuilder.getShaders().mapValues { GlslAnalyzer().asShader(it.value) },
            showBuilder.getDataSources()
        )
    }
}

data class LinkEditor(
    val from: Port,
    val to: Port
) {
    constructor(from: PortRef, to: PortRef, show: ShowEditor) :
            this(from.dereference(show), to.dereference(show))

    fun toRef(showBuilder: ShowBuilder): Link =
        Link(from.toRef(showBuilder), to.toRef(showBuilder))

    interface Port {
        fun toRef(showBuilder: ShowBuilder): PortRef

        infix fun linkTo(other: Port): LinkEditor = LinkEditor(this, other)
    }
}

data class DataSourceEditor(val dataSource: DataSource) : LinkEditor.Port {
    override fun toRef(showBuilder: ShowBuilder): PortRef =
        DataSourceRef(showBuilder.idFor(dataSource))
}

data class ShaderEditor(val shader: Shader) {
    fun inputPort(portId: String): LinkEditor.Port = ShaderInPortEditor(shader, portId)
    fun outputPort(portId: String): LinkEditor.Port = ShaderOutPortEditor(shader, portId)

    data class ShaderInPortEditor(override val shader: Shader, private val portId: String) : ShaderPortEditor {
        override fun toRef(showBuilder: ShowBuilder): PortRef =
            ShaderInPortRef(showBuilder.idFor(shader), portId)

        override fun toString(): String = "ShaderInPortEditor(shader=${shader.title} port=$portId)"
    }

    data class ShaderOutPortEditor(override val shader: Shader, private val portId: String) : ShaderPortEditor {
        override fun toRef(showBuilder: ShowBuilder): PortRef =
            ShaderOutPortRef(showBuilder.idFor(shader), portId)

        override fun toString(): String = "ShaderOutPortEditor(shader=${shader.title} port=$portId)"
    }
}

interface ShaderPortEditor : LinkEditor.Port {
    val shader: Shader
}

data class OutputPortEditor(private val portId: String) : LinkEditor.Port {
    override fun toRef(showBuilder: ShowBuilder): PortRef =
        OutputPortRef(portId)
}

class ShowBuilder {
    private val dataSourceIds = Ids<DataSource>()
    private val shaderIds = Ids<Shader>()

    fun idFor(dataSource: DataSource): String {
        return dataSourceIds.idFor(dataSource) { dataSource.suggestId() }
    }

    fun idFor(shader: Shader): String {
        return shaderIds.idFor(shader) { shader.suggestId() }
    }

    fun getDataSources(): Map<String, DataSource> = dataSourceIds.byId
    fun getShaders(): Map<String, Shader> = shaderIds.byId

    class Ids<T> {
        private val toId = mutableMapOf<T, String>()
        internal val byId = mutableMapOf<String, T>()

        fun idFor(t: T, suggest: () -> String): String {
            return toId.getOrPut(t) {
                val suggestedId = suggest()
                if (!byId.containsKey(suggestedId)) {
                    byId[suggestedId] = t
                    return@getOrPut suggestedId
                }

                var i = 2
                while (byId.containsKey("${suggestedId}$i")) i++
                byId["${suggestedId}$i"] = t
                "${suggestedId}$i"
            }
        }
    }
}