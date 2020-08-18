package baaahs.shows

import baaahs.Gadget
import baaahs.gadgets.ColorPicker
import baaahs.gadgets.Slider
import baaahs.plugin.CorePlugin
import baaahs.plugin.Plugins
import baaahs.show.*
import kotlinx.serialization.json.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

@Suppress("unused")
object ShowSerializationSpec : Spek({
    describe("Show serialization") {
        val plugins by value { Plugins.safe() }
        val jsonPrettyPrint by value {
            Json(
                JsonConfiguration.Stable.copy(prettyPrint = true),
                plugins.serialModule
            )
        }
        val origShow by value { SampleData.sampleShow }
        val showJson by value { origShow.toJson(plugins) }

        context("to json") {
            it("serializes") {
                expectJson(forJson(origShow)) { showJson }
            }
        }

        context("fromJson") {
            it("deserializes equally") {
                expectJson(forJson(origShow)) {
                    val jsonStr = jsonPrettyPrint.stringify(JsonElement.serializer(), origShow.toJson(plugins))
                    forJson(Show.fromJson(plugins, jsonStr))
                }
            }
        }
    }
})

private fun JsonObjectBuilder.mapTo(k: String, v: JsonElement) = k to v

private fun JsonObjectBuilder.addPatchHolder(patchHolder: PatchHolder) {
    "title" to patchHolder.title
    "patches" to patchHolder.patches.jsonMap { jsonFor(it) }
    "eventBindings" to patchHolder.eventBindings.jsonMap { jsonFor(it) }
    "controlLayout" to patchHolder.controlLayout.jsonMap { it.jsonMap { JsonPrimitive(it) } }
}

private fun <V> Map<String, V>.jsonMap(block: JsonObjectBuilder.(V) -> JsonElement): JsonObject {
    return json { entries.forEach { (k, v) -> k to block(v) } }
}

private fun <T> List<T>.jsonMap(block: (T) -> JsonElement): JsonArray {
    return jsonArray { forEach { +block(it) } }
}

private fun forJson(show: Show): JsonObject {
    return json {
        addPatchHolder(show)
        "scenes" to show.scenes.jsonMap { jsonFor(it) }
        "layouts" to json {
            "panelNames" to show.layouts.panelNames.jsonMap { JsonPrimitive(it) }
            "map" to show.layouts.map.jsonMap {
                json { "rootNode" to it.rootNode }
            }
        }
        "shaders" to show.shaders.jsonMap { jsonFor(it) }
        "shaderInstances" to show.shaderInstances.jsonMap { jsonFor(it) }
        "controls" to show.controls.jsonMap { jsonFor(it) }
        "dataSources" to show.dataSources.jsonMap { jsonFor(it) }
    }
}

private fun jsonFor(scene: Scene): JsonObject {
    return json {
        addPatchHolder(scene)
        "patchSets" to jsonArray {
            for (it in scene.patchSets) {
                +jsonFor(it)
            }
        }
    }
}

fun jsonFor(patchSet: PatchSet): JsonElement {
    return json {
        addPatchHolder(patchSet)
    }

}

private fun jsonFor(eventBinding: EventBinding) = json { }

fun jsonFor(control: Control): JsonElement {
    return when (control) {
        is GadgetControl -> json {
            "type" to "baaahs.Core:Gadget"
            "gadget" to jsonFor(control.gadget)
            "controlledDataSourceId" to control.controlledDataSourceId
        }
        is ButtonGroupControl -> json {
            "type" to "baaahs.Core:ButtonGroup"
            "title" to control.title
        }
        else -> json { "type" to "unknown" }
    }
}

fun jsonFor(gadget: Gadget): JsonElement {
    return when (gadget) {
        is Slider -> json {
            "type" to "baaahs.Core:Slider"
            "title" to gadget.title
            "initialValue" to gadget.initialValue
            "minValue" to gadget.minValue
            "maxValue" to gadget.maxValue
            "stepValue" to gadget.stepValue
        }
        is ColorPicker -> json {
            "type" to "baaahs.Core:ColorPicker"
            "title" to gadget.title
            "initialValue" to gadget.initialValue.toInt()
        }
        else -> json { "type" to "unknown" }
    }

}

fun jsonFor(dataSource: DataSource): JsonElement {
    return when (dataSource) {
        is CorePlugin.SliderDataSource -> {
            json {
                "type" to "baaahs.Core:Slider"
                "title" to dataSource.title
                "initialValue" to dataSource.initialValue
                "minValue" to dataSource.minValue
                "maxValue" to dataSource.maxValue
                "stepValue" to dataSource.stepValue
            }
        }
        is CorePlugin.ColorPickerDataSource -> {
            json {
                "type" to "baaahs.Core:ColorPicker"
                "title" to dataSource.title
                "initialValue" to dataSource.initialValue.toInt()
            }
        }
        is CorePlugin.ResolutionDataSource -> {
            json {
                "type" to "baaahs.Core:Resolution"
            }
        }
        is CorePlugin.TimeDataSource -> {
            json {
                "type" to "baaahs.Core:Time"
            }
        }
        is CorePlugin.PixelCoordsTextureDataSource -> {
            json {
                "type" to "baaahs.Core:PixelCoordsTexture"
            }
        }
        is CorePlugin.ModelInfoDataSource -> {
            json {
                "type" to "baaahs.Core:ModelInfo"
            }
        }
        else -> json { "type" to "unknown" }
    }
}

private fun jsonFor(patch: Patch): JsonObject {
    return json {
        "shaderInstanceIds" to patch.shaderInstanceIds.jsonMap { JsonPrimitive(it) }
        "surfaces" to json {
            "name" to "All Surfaces"
        }
    }
}

private fun jsonFor(portRef: PortRef): JsonObject {
    return when (portRef) {
        is DataSourceRef -> json {
            "type" to "datasource"
            "dataSourceId" to portRef.dataSourceId
        }
        is ShaderOutPortRef -> json {
            "type" to "shader-out"
            "shaderInstanceId" to portRef.shaderInstanceId
        }
        is ShaderChannelRef -> json {
            "type" to "shader-channel"
            "shaderChannel" to portRef.shaderChannel.id
        }
        is OutputPortRef -> json {
            "type" to "output"
            "portId" to portRef.portId
        }
        else -> error("huh? $portRef")
    }
}

private fun jsonFor(shader: Shader) = json {
    "title" to shader.title
    "type" to shader.type.name
    "src" to shader.src
}

private fun jsonFor(shaderInstance: ShaderInstance) = json {
    "shaderId" to shaderInstance.shaderId
    "incomingLinks" to shaderInstance.incomingLinks.jsonMap { jsonFor(it) }
    "shaderChannel" to shaderInstance.shaderChannel.id
    "priority" to shaderInstance.priority
}

fun expectJson(expected: JsonElement, block: () -> JsonElement) {
    val json = Json(JsonConfiguration.Stable.copy(prettyPrint = true), Plugins.safe().serialModule)
    fun JsonElement.toStr() = json.stringify(JsonElementSerializer, this)
    kotlin.test.expect(expected.toStr()) { block().toStr() }
}
