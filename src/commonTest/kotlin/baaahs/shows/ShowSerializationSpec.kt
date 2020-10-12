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
            Json {
                prettyPrint = true
                serializersModule = plugins.serialModule
            }
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
                    val jsonStr = jsonPrettyPrint.encodeToString(JsonElement.serializer(), origShow.toJson(plugins))
                    forJson(Show.fromJson(plugins, jsonStr))
                }
            }
        }
    }
})

private fun JsonObjectBuilder.mapTo(k: String, v: JsonElement) = put(k, v)

private fun JsonObjectBuilder.addPatchHolder(patchHolder: PatchHolder) {
    put("patches", patchHolder.patches.jsonMap { jsonFor(it) })
    put("eventBindings", patchHolder.eventBindings.jsonMap { jsonFor(it) })
    put("controlLayout", patchHolder.controlLayout.jsonMap { it.jsonMap { JsonPrimitive(it) } })
}

private fun <V> Map<String, V>.jsonMap(block: JsonObjectBuilder.(V) -> JsonElement): JsonObject {
    return buildJsonObject { entries.forEach { (k, v) -> put(k, block(v)) } }
}

private fun <T> List<T>.jsonMap(block: (T) -> JsonElement): JsonArray {
    return buildJsonArray { forEach { add(block(it)) } }
}

private fun forJson(show: Show): JsonObject {
    return buildJsonObject {
        put("title", show.title)
        addPatchHolder(show)
        put("layouts", buildJsonObject {
            put("panelNames", show.layouts.panelNames.jsonMap { JsonPrimitive(it) })
            put("map", show.layouts.map.jsonMap {
                buildJsonObject { put("rootNode", it.rootNode) }
            })
        })
        put("shaders", show.shaders.jsonMap { jsonFor(it) })
        put("shaderInstances", show.shaderInstances.jsonMap { jsonFor(it) })
        put("controls", show.controls.jsonMap { jsonFor(it) })
        put("dataSources", show.dataSources.jsonMap { jsonFor(it) })
    }
}

private fun jsonFor(eventBinding: EventBinding) = buildJsonObject { }

fun jsonFor(control: Control): JsonElement {
    return when (control) {
        is GadgetControl -> buildJsonObject {
            put("type", "baaahs.Core:Gadget")
            put("gadget", jsonFor(control.gadget))
            put("controlledDataSourceId", control.controlledDataSourceId)
        }
        is ButtonGroupControl -> buildJsonObject {
            put("type", "baaahs.Core:ButtonGroup")
            put("title", control.title)
            put("direction", control.direction.name)
            put("buttonIds", control.buttonIds.jsonMap { JsonPrimitive(it) })
        }
        is ButtonControl -> buildJsonObject {
            put("type", "baaahs.Core:Button")
            put("title", control.title)
            put("activationType", control.activationType.name)
            addPatchHolder(control)
        }
        else -> buildJsonObject { put("type", "unknown") }
    }
}

fun jsonFor(gadget: Gadget): JsonElement {
    return when (gadget) {
        is Slider -> buildJsonObject {
            put("type", "baaahs.Core:Slider")
            put("title", gadget.title)
            put("initialValue", gadget.initialValue)
            put("minValue", gadget.minValue)
            put("maxValue", gadget.maxValue)
            put("stepValue", gadget.stepValue)
        }
        is ColorPicker -> buildJsonObject {
            put("type", "baaahs.Core:ColorPicker")
            put("title", gadget.title)
            put("initialValue", gadget.initialValue.toInt())
        }
        else -> buildJsonObject { put("type", "unknown") }
    }

}

fun jsonFor(dataSource: DataSource): JsonElement {
    return when (dataSource) {
        is CorePlugin.SliderDataSource -> {
            buildJsonObject {
                put("type", "baaahs.Core:Slider")
                put("title", dataSource.title)
                put("initialValue", dataSource.initialValue)
                put("minValue", dataSource.minValue)
                put("maxValue", dataSource.maxValue)
                put("stepValue", dataSource.stepValue)
            }
        }
        is CorePlugin.ColorPickerDataSource -> {
            buildJsonObject {
                put("type", "baaahs.Core:ColorPicker")
                put("title", dataSource.title)
                put("initialValue", dataSource.initialValue.toInt())
            }
        }
        is CorePlugin.ResolutionDataSource -> {
            buildJsonObject {
                put("type", "baaahs.Core:Resolution")
            }
        }
        is CorePlugin.TimeDataSource -> {
            buildJsonObject {
                put("type", "baaahs.Core:Time")
            }
        }
        is CorePlugin.PixelCoordsTextureDataSource -> {
            buildJsonObject {
                put("type", "baaahs.Core:PixelCoordsTexture")
            }
        }
        is CorePlugin.ModelInfoDataSource -> {
            buildJsonObject {
                put("type", "baaahs.Core:ModelInfo")
            }
        }
        else -> buildJsonObject { put("type", "unknown") }
    }
}

private fun jsonFor(patch: Patch): JsonObject {
    return buildJsonObject {
        put("shaderInstanceIds", patch.shaderInstanceIds.jsonMap { JsonPrimitive(it) })
        put("surfaces", buildJsonObject {
            put("name", "All Surfaces")
        })
    }
}

private fun jsonFor(portRef: PortRef): JsonObject {
    return when (portRef) {
        is DataSourceRef -> buildJsonObject {
            put("type", "datasource")
            put("dataSourceId", portRef.dataSourceId)
        }
        is ShaderOutPortRef -> buildJsonObject {
            put("type", "shader-out")
            put("shaderInstanceId", portRef.shaderInstanceId)
        }
        is ShaderChannelRef -> buildJsonObject {
            put("type", "shader-channel")
            put("shaderChannel", portRef.shaderChannel.id)
        }
        is OutputPortRef -> buildJsonObject {
            put("type", "output")
            put("portId", portRef.portId)
        }
        else -> error("huh? $portRef")
    }
}

private fun jsonFor(shader: Shader) = buildJsonObject {
    put("title", shader.title)
    put("type", shader.type.name)
    put("src", shader.src)
}

private fun jsonFor(shaderInstance: ShaderInstance) = buildJsonObject {
    put("shaderId", shaderInstance.shaderId)
    put("incomingLinks", shaderInstance.incomingLinks.jsonMap { jsonFor(it) })
    put("shaderChannel", shaderInstance.shaderChannel.id)
    put("priority", shaderInstance.priority)
}

fun expectJson(expected: JsonElement, block: () -> JsonElement) {
    val json = Json {
        prettyPrint = true
        serializersModule = Plugins.safe().serialModule
    }
    fun JsonElement.toStr() = json.encodeToString(JsonElement.serializer(), this)
    kotlin.test.expect(expected.toStr()) { block().toStr() }
}
