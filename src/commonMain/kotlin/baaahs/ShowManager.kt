package baaahs

import baaahs.gadgets.ColorPicker
import baaahs.gadgets.RadioButtonStrip
import baaahs.gadgets.Slider
import baaahs.glshaders.GlslAnalyzer
import baaahs.glshaders.GlslProgram
import baaahs.glshaders.Plugins
import baaahs.glshaders.ShaderFragment
import baaahs.glsl.GlslContext
import baaahs.show.PatchSet
import baaahs.show.Scene
import baaahs.show.Show
import com.soywiz.klock.DateTime
import kotlinx.serialization.Serializable
import kotlin.math.min

@Serializable
data class ShowState(
    val selectedScene: Int,
    val patchSetSelections: List<Int>
) {
    val selectedPatchSet: Int get() = patchSetSelections[selectedScene]

    fun findPatchSet(show: Show): PatchSet {
        if (selectedScene >= show.scenes.size) {
            error("scene $selectedScene out of bounds (have ${show.scenes.size})")
        }

        val scene = show.scenes[selectedScene]
        if (selectedScene >= patchSetSelections.size) {
            error("scene $selectedScene patch set out of bounds (have ${patchSetSelections.size})")
        }

        if (selectedPatchSet >= scene.patchSets.size) {
            error("patch set $selectedPatchSet patch set out of bounds " +
                    "(have ${patchSetSelections.size} for scene $selectedScene)")
        }

        return scene.patchSets[selectedPatchSet]
    }

    fun withScene(i: Int) = copy(selectedScene = i)
    fun withPatchSet(i: Int) = copy(patchSetSelections = patchSetSelections.replacing(selectedScene, i))
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
                    scene.patchSets.size - 1)
            }
        )
    }

    companion object {
        fun forShow(show: Show): ShowState = ShowState(0, show.scenes.map { 0 })
    }
}

interface ShowResources {
    val plugins: Plugins
    val glslContext: GlslContext
    val showWithStateTopic: PubSub.Topic<ShowWithState>
    val dataFeeds: Map<String, GlslProgram.DataFeed>
    val shaders: Map<String, ShaderFragment>

    fun <T : Gadget> createdGadget(id: String, gadget: T)
    fun <T : Gadget> useGadget(id: String): T

    fun createShowWithStateTopic(): PubSub.Topic<ShowWithState> {
        return PubSub.Topic("showWithState", ShowWithState.serializer(), plugins.serialModule)
    }
}

@Serializable
data class ShowWithState(val show: Show, val showState: ShowState)

fun Show.withState(showState: ShowState): ShowWithState {
    return ShowWithState(this, showState.boundedBy(this))
}

interface MutableShowResources : ShowResources {
    fun switchTo(show: Show)
}

abstract class BaseShowResources(
    final override val plugins: Plugins,
    initialShow: Show
) : MutableShowResources {
    val glslAnalyzer = GlslAnalyzer()
    override val showWithStateTopic: PubSub.Topic<ShowWithState> by lazy { createShowWithStateTopic() }

    override val dataFeeds: MutableMap<String, GlslProgram.DataFeed> by lazy {
        calculateDataFeeds(initialShow).toMutableMap()
    }

    private fun calculateDataFeeds(show: Show): Map<String, GlslProgram.DataFeed> {
        PubSub.stopHere()
        return show.dataSources.associate { dataSourceProvider ->
            dataSourceProvider.id to dataSourceProvider.create(this)
        }
    }

    override val shaders: MutableMap<String, ShaderFragment> by lazy {
        calculateShaders(initialShow).toMutableMap()
    }

    private fun calculateShaders(show: Show) =
        show.shaderFragments.mapValues { (_, src) -> glslAnalyzer.asShader(src) }

    override fun switchTo(show: Show) {
        // TODO: Do something more efficient here.
        dataFeeds.values.forEach { it.release() }
        dataFeeds.clear()
        dataFeeds.putAll(calculateDataFeeds(show))

        println("Switch to ${show.title}; old shaders: ${shaders.keys}")
        shaders.clear()
        val newShaders = calculateShaders(show)
        println("  new shaders: ${newShaders.keys}")
        shaders.putAll(newShaders)
    }
}

class ClientShowResources(
    plugins: Plugins,
    override val glslContext: GlslContext,
    show: Show
) : BaseShowResources(plugins, show) {
    private val gadgets: MutableMap<String, Gadget> = mutableMapOf()

    override fun <T : Gadget> createdGadget(id: String, gadget: T) {
        gadgets[id] = gadget
    }

    override fun <T : Gadget> useGadget(id: String): T {
        return gadgets[id] as T
    }
}

class ShowManager(
    plugins: Plugins,
    override val glslContext: GlslContext,
    val pubSub: PubSub.Server,
    show: Show
) : BaseShowResources(plugins, show) {
    private val gadgets: MutableMap<String, GadgetManager.GadgetInfo> = mutableMapOf()
    var lastUserInteraction = DateTime.now()

    var currentScene = show.scenes.first()
    var currentPatchSet = currentScene.patchSets.first()

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