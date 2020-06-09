package baaahs

import baaahs.gadgets.ColorPicker
import baaahs.gadgets.RadioButtonStrip
import baaahs.gadgets.Slider
import baaahs.glshaders.GlslAnalyzer
import baaahs.glshaders.GlslProgram
import baaahs.glshaders.ShaderFragment
import baaahs.glsl.GlslContext
import baaahs.show.PatchSet
import baaahs.show.Scene
import baaahs.show.Show
import com.soywiz.klock.DateTime
import kotlinx.serialization.Serializable

@Serializable
data class ShowState(
    val selectedScene: Int,
    val patchSetSelections: List<Int>
) {
    val selectedPatchSet: Int get() = patchSetSelections[selectedScene]

    fun findPatchSet(show: Show): PatchSet {
        return show.scenes[selectedScene].patchSets[selectedPatchSet]
    }

    fun withScene(i: Int) = copy(selectedScene = i)
    fun withPatchSet(i: Int) = copy(patchSetSelections = patchSetSelections.replacing(selectedScene, i))
}

interface ShowResources {
    val glslContext: GlslContext
    val dataSources: Map<String, GlslProgram.DataFeed>
    val shaders: Map<String, ShaderFragment>

    fun <T : Gadget> createdGadget(id: String, gadget: T)
    fun <T : Gadget> useGadget(id: String): T
}

class ShowManager(
    val show: Show,
    val pubSub: PubSub.Server,
    override val glslContext: GlslContext
) : ShowResources {
    override val shaders = show.shaderFragments.mapValues { (_, src) ->
        GlslAnalyzer().asShader(src)
    }
    private val gadgets: MutableMap<String, GadgetManager.GadgetInfo> = mutableMapOf()
    var lastUserInteraction = DateTime.now()

    override val dataSources =
        show.dataSources.associate { dataSourceProvider ->
            dataSourceProvider.id to dataSourceProvider.create(this)
        }

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