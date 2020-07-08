package baaahs

import baaahs.gadgets.ColorPicker
import baaahs.gadgets.RadioButtonStrip
import baaahs.gadgets.Slider
import baaahs.glshaders.*
import baaahs.glsl.GlslContext
import baaahs.glsl.GlslRenderer
import baaahs.model.ModelInfo
import baaahs.show.*
import com.soywiz.klock.DateTime
import kotlinx.serialization.Serializable
import kotlin.math.min

@Serializable
data class ShowState(
    val selectedScene: Int,
    val patchSetSelections: List<Int>
) {
    val selectedPatchSet: Int get() = patchSetSelections[selectedScene]

    fun findScene(show: OpenShow): OpenShow.OpenScene? {
        if (selectedScene == -1) return null

        if (selectedScene >= show.scenes.size) {
            error("scene $selectedScene out of bounds (have ${show.scenes.size})")
        }

        return show.scenes[selectedScene]
    }

    fun findPatchSet(show: OpenShow): OpenShow.OpenScene.OpenPatchSet? {
        if (selectedPatchSet == -1) return null

        val scene = findScene(show) ?: return null

        if (selectedScene >= patchSetSelections.size) {
            error("scene $selectedScene patch set out of bounds (have ${patchSetSelections.size})")
        }

        if (selectedPatchSet >= scene.patchSets.size) {
            error(
                "patch set $selectedPatchSet out of bounds " +
                        "(have ${patchSetSelections.size} for scene $selectedScene)"
            )
        }

        return scene.patchSets[selectedPatchSet]
    }

    fun selectScene(i: Int) = copy(selectedScene = i)
    fun selectPatchSet(i: Int) = copy(patchSetSelections = patchSetSelections.replacing(selectedScene, i))
    fun withPatchSetSelections(selections: List<Int>) = copy(patchSetSelections = selections)

    /**
     * Returns a ShowState whose parameters fit within the specified [Show].
     */
    fun boundedBy(show: Show): ShowState {
        return ShowState(
            selectedScene = min(selectedScene, show.scenes.size - 1),
            patchSetSelections = show.scenes.mapIndexed { index, scene ->
                min(
                    patchSetSelections.getOrNull(index) ?: 0,
                    scene.patchSets.size - 1
                )
            }
        )
    }

    companion object {
        val Empty: ShowState = ShowState(0, emptyList())

        fun forShow(show: Show): ShowState = ShowState(0, show.scenes.map { 0 })
    }
}

interface ShowResources {
    val plugins: Plugins
    val glslContext: GlslContext
    val modelInfo: ModelInfo
    val showWithStateTopic: PubSub.Topic<ShowWithState>

    fun <T : Gadget> createdGadget(id: String, gadget: T)
    fun <T : Gadget> useGadget(id: String): T

    fun openShader(shader: Shader): OpenShader
    fun openDataFeed(id: String, dataSource: DataSource): GlslProgram.DataFeed
    fun useDataFeed(dataSource: DataSource): GlslProgram.DataFeed

    fun createShowWithStateTopic(): PubSub.Topic<ShowWithState> {
        return PubSub.Topic("showWithState", ShowWithState.serializer(), plugins.serialModule)
    }

    fun releaseUnused()

    fun swapAndRelease(oldOpenShow: OpenShow?, newShow: Show): OpenShow {
        val newOpenShow = OpenShow(newShow, this)
        oldOpenShow?.release()
        releaseUnused()
        return newOpenShow
    }
}

@Serializable
data class ShowWithState(val show: Show, val showState: ShowState)

fun Show.withState(showState: ShowState): ShowWithState {
    return ShowWithState(this, showState.boundedBy(this))
}

abstract class BaseShowResources(
    final override val plugins: Plugins,
    final override val modelInfo: ModelInfo
) : ShowResources {
    private val glslAnalyzer = GlslAnalyzer()

    override val showWithStateTopic: PubSub.Topic<ShowWithState> by lazy { createShowWithStateTopic() }

    private val dataFeeds = mutableMapOf<DataSource, GlslProgram.DataFeed>()
    private val shaders = mutableMapOf<Shader, OpenShader>()

    override fun openDataFeed(id: String, dataSource: DataSource): GlslProgram.DataFeed {
        return dataFeeds.getOrPut(dataSource) { dataSource.createFeed(this, id) }
    }

    override fun useDataFeed(dataSource: DataSource): GlslProgram.DataFeed {
        return dataFeeds[dataSource]!!
    }

    override fun openShader(shader: Shader): OpenShader {
        return shaders.getOrPut(shader) { glslAnalyzer.asShader(shader) }
    }

    override fun releaseUnused() {
        ArrayList(dataFeeds.entries).forEach { (dataSource, dataFeed) ->
            if (!dataFeed.inUse()) dataFeeds.remove(dataSource)
        }

        ArrayList(shaders.entries).forEach { (shader, openShader) ->
            if (!openShader.inUse()) shaders.remove(shader)
        }
    }
}

class ShowManager(
    plugins: Plugins,
    override val glslContext: GlslContext,
    val pubSub: PubSub.Server,
    modelInfo: ModelInfo
) : BaseShowResources(plugins, modelInfo) {
    private val gadgets: MutableMap<String, GadgetManager.GadgetInfo> = mutableMapOf()
    var lastUserInteraction = DateTime.now()

    override fun <T : Gadget> createdGadget(id: String, gadget: T) {
        val topic =
            PubSub.Topic("/gadgets/$id", GadgetDataSerializer)
        val channel = pubSub.publish(topic, gadget.state) { updated ->
            gadget.state.putAll(updated)
            lastUserInteraction = DateTime.now()
        }
        val gadgetChannelListener: (Gadget) -> Unit = { gadget1 ->
            channel.onChange(gadget1.state)
        }
        gadget.listen(gadgetChannelListener)
        val gadgetData = GadgetData(id, gadget, topic.name)
        gadgets[id] = GadgetManager.GadgetInfo(topic, channel, gadgetData, gadgetChannelListener)
    }

    override fun <T : Gadget> useGadget(id: String): T {
        return (gadgets[id]?.gadgetData?.gadget
            ?: error("no such gadget \"$id\" among [${gadgets.keys.sorted()}]")) as T
    }
}

interface RefCounted {
    fun inUse(): Boolean
    fun use()
    fun release()
    fun onFullRelease()
}

class RefCounter : RefCounted {
    var refCount: Int = 0

    override fun inUse(): Boolean = refCount == 0

    override fun use() {
        refCount++
    }

    override fun release() {
        refCount--

        if (!inUse()) onFullRelease()
    }

    override fun onFullRelease() {
    }
}

open class OpenControllables(
    patchy: Patchy, private val dataSources: Map<String, DataSource>
) {
    val controlLayout: Map<String, List<DataSource>> = patchy.controlLayout.mapValues { (_, dataSourceRefs) ->
        dataSourceRefs.map { dataSources.getBang(it.dataSourceId, "datasource") }
    }
}

class OpenShow(
    private val show: Show, private val showResources: ShowResources
) : RefCounted by RefCounter(), OpenControllables(show, show.dataSources) {
    val layouts get() = show.layouts
    val shaders = show.shaders.mapValues { (_, shader) -> showResources.openShader(shader) }

    val dataFeeds = show.dataSources.entries.associate { (id, dataSource) ->
        val dataFeed = showResources.openDataFeed(id, dataSource)
        id to dataFeed
    }
    val scenes = show.scenes.map { OpenScene(it) }

    fun edit(showState: ShowState, block: ShowEditor.() -> Unit): ShowEditor =
        ShowEditor(show, showState).apply(block)

    override fun onFullRelease() {
        shaders.values.forEach { it.release() }
        dataFeeds.values.forEach { it.release() }
    }

    inner class OpenScene(scene: Scene) : OpenControllables(scene, show.dataSources) {
        val title = scene.title
        val patchSets = scene.patchSets.map { OpenPatchSet(it) }

        inner class OpenPatchSet(patchSet: PatchSet) : OpenControllables(patchSet, show.dataSources) {
            val title = patchSet.title
            val patches = patchSet.patches.map { OpenPatch(it, shaders, show.dataSources) }

            fun createRenderPlan(glslContext: GlslContext): RenderPlan {
                val activeDataSources = mutableSetOf<String>()
                val programs = patches.map { patch ->
                    patch to patch.createProgram(glslContext, dataFeeds)
                }
                return RenderPlan(programs)

            }
        }
    }
}

class RenderPlan(val programs: List<Pair<OpenPatch, GlslProgram>>) {
    fun render(glslRenderer: GlslRenderer) {
        glslRenderer.draw()
    }
}

val controlTypes = listOf(
    SliderType,
    ColorPickerType,
    RadioButtonStripType,
    SceneListType,
    PatchSetListType
).associateBy { it.name }

interface ShowController {
    val scenes: List<Scene>
    var currentSceneIndex: Int
    var currentPatchSetIndex: Int
}

object SliderType : ControlType("Slider") {
    override fun createGadget(
        name: String,
        data: Map<String, Any?>,
        showController: ShowController
    ): Gadget {
        return Slider(
            name,
            initialValue = data["default"] as? Float ?: 1f,
            minValue = data["min"] as? Float ?: 0f,
            maxValue = data["max"] as? Float ?: 1f,
            stepValue = data["step"] as? Float ?: 0.01f
        )
    }
}

object ColorPickerType : ControlType("ColorPicker") {
    override fun createGadget(
        name: String,
        data: Map<String, Any?>,
        showController: ShowController
    ): Gadget {
        return ColorPicker(
            name,
            initialValue = data["default"] as? Color ?: Color.WHITE
        )
    }
}

object SceneListType : ControlType("SceneList") {
    override fun createGadget(
        name: String,
        data: Map<String, Any?>,
        showController: ShowController
    ): Gadget {
        return RadioButtonStrip(
            name,
            showController.scenes.map { it.title },
            showController.currentSceneIndex
        )
    }
}

object PatchSetListType : ControlType("PatchSetList") {
    override fun createGadget(
        name: String,
        data: Map<String, Any?>,
        showController: ShowController
    ): Gadget {
        return RadioButtonStrip(
            name,
            showController
                .scenes[showController.currentSceneIndex]
                .patchSets.map { it.title },
            showController.currentPatchSetIndex
        )
    }
}

object RadioButtonStripType : ControlType("RadioButtonStrip") {
    override fun createGadget(
        name: String,
        data: Map<String, Any?>,
        showController: ShowController
    ): Gadget {
        return RadioButtonStrip(
            name,
            (data["options"] as Array<*>?)?.map { it.toString() } ?: emptyList(),
            0
        )
    }

}

abstract class ControlType(val name: String) {
    abstract fun createGadget(
        name: String,
        data: Map<String, Any?>,
        showController: ShowController
    ): Gadget
}