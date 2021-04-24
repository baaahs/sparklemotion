package baaahs.shows

import baaahs.control.ButtonControl
import baaahs.control.ButtonGroupControl
import baaahs.control.ColorPickerControl
import baaahs.control.SliderControl
import baaahs.fixtures.PixelLocationDataSource
import baaahs.gl.kexpect
import baaahs.plugin.Plugins
import baaahs.plugin.beatlink.BeatLinkControl
import baaahs.plugin.beatlink.BeatLinkPlugin
import baaahs.plugin.core.datasource.*
import baaahs.show.*
import kotlinx.serialization.json.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

@Suppress("unused")
object ShowSerializationSpec : Spek({
    describe("Show serialization") {
        val plugins by value { SampleData.plugins }
        val jsonWithDefaults by value { Json(plugins.json) { encodeDefaults = true } }
        val jsonPrettyPrint by value { Json(plugins.json) { prettyPrint = true } }
        val origShow by value { SampleData.sampleShowWithBeatLink }
        val showJson by value { origShow.toJson(jsonWithDefaults) }

        context("to json") {
            it("serializes") {
                plugins.expectJson(forJson(origShow)) { showJson }
            }
        }

        context("fromJson") {
            it("deserializes equally") {
                plugins.expectJson(forJson(origShow)) {
                    val jsonStr = jsonPrettyPrint.encodeToString(
                        JsonElement.serializer(), origShow.toJson(jsonWithDefaults))
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
            put("panels", buildJsonObject {
                show.layouts.panels.forEach { (title, info) ->
                    put(title, jsonFor(info))
                }
            })
            put("formats", show.layouts.formats.jsonMap {
                buildJsonObject {
                    put("mediaQuery", it.mediaQuery)
                    put("tabs", it.tabs.jsonMap {
                        buildJsonObject {
                            put("title", it.title)
                            put("columns", it.columns.jsonMap { JsonPrimitive(it) })
                            put("rows", it.rows.jsonMap { JsonPrimitive(it) })
                            put("areas", it.areas.jsonMap { JsonPrimitive(it) })
                        }
                    })
                }
            })
        })
        put("shaders", show.shaders.jsonMap { jsonFor(it) })
        put("shaderInstances", show.shaderInstances.jsonMap { jsonFor(it) })
        put("controls", show.controls.jsonMap { jsonFor(it) })
        put("dataSources", show.dataSources.jsonMap { jsonFor(it) })
    }
}

private fun jsonFor(eventBinding: EventBinding) = buildJsonObject { }

fun jsonFor(panel: Panel): JsonElement {
    return buildJsonObject {
        put("title", panel.title)
    }
}

fun jsonFor(control: Control): JsonElement {
    return when (control) {
        is BeatLinkControl -> buildJsonObject {
            put("type", "baaahs.BeatLink:BeatLink")
        }
        is ButtonControl -> buildJsonObject {
            put("type", "baaahs.Core:Button")
            put("title", control.title)
            put("activationType", control.activationType.name)
            addPatchHolder(control)
            put("controlledDataSourceId", control.controlledDataSourceId)
        }
        is ButtonGroupControl -> buildJsonObject {
            put("type", "baaahs.Core:ButtonGroup")
            put("title", control.title)
            put("direction", control.direction.name)
            put("buttonIds", control.buttonIds.jsonMap { JsonPrimitive(it) })
        }
        is ColorPickerControl -> buildJsonObject {
            put("type", "baaahs.Core:ColorPicker")
            put("title", control.title)
            put("initialValue", control.initialValue.toInt())
            put("controlledDataSourceId", control.controlledDataSourceId)
        }
        is SliderControl -> buildJsonObject {
            put("type", "baaahs.Core:Slider")
            put("title", control.title)
            put("initialValue", control.initialValue)
            put("minValue", control.minValue)
            put("maxValue", control.maxValue)
            put("stepValue", control.stepValue)
            put("controlledDataSourceId", control.controlledDataSourceId)
        }
        else -> buildJsonObject { put("type", "unknown") }
    }
}

fun jsonFor(dataSource: DataSource): JsonElement {
    return when (dataSource) {
        is SliderDataSource -> {
            buildJsonObject {
                put("type", "baaahs.Core:Slider")
                put("title", dataSource.sliderTitle)
                put("initialValue", dataSource.initialValue)
                put("minValue", dataSource.minValue)
                put("maxValue", dataSource.maxValue)
                put("stepValue", dataSource.stepValue)
            }
        }
        is ColorPickerDataSource -> {
            buildJsonObject {
                put("type", "baaahs.Core:ColorPicker")
                put("title", dataSource.colorPickerTitle)
                put("initialValue", dataSource.initialValue.toInt())
            }
        }
        is ResolutionDataSource -> {
            buildJsonObject {
                put("type", "baaahs.Core:Resolution")
            }
        }
        is TimeDataSource -> {
            buildJsonObject {
                put("type", "baaahs.Core:Time")
            }
        }
        is PixelCoordsTextureDataSource -> {
            buildJsonObject {
                put("type", "baaahs.Core:PixelCoordsTexture")
            }
        }
        is PixelLocationDataSource -> {
            buildJsonObject {
                put("type", "baaahs.Core:PixelLocation")
            }
        }
        is ModelInfoDataSource -> {
            buildJsonObject {
                put("type", "baaahs.Core:ModelInfo")
            }
        }
        is RasterCoordinateDataSource -> {
            buildJsonObject {
                put("type", "baaahs.Core:RasterCoordinate")
            }
        }
        is BeatLinkPlugin.BeatLinkDataSource -> buildJsonObject {
            put("type", "baaahs.BeatLink:BeatLink")
        }
        else -> buildJsonObject { put("type", "unknown") }
    }
}

private fun jsonFor(patch: Patch): JsonObject {
    return buildJsonObject {
        put("shaderInstanceIds", patch.shaderInstanceIds.jsonMap { JsonPrimitive(it) })
        put("surfaces", buildJsonObject {
            put("name", "All Surfaces")
            put("deviceTypes", buildJsonArray { })
        })
    }
}

private fun jsonFor(portRef: PortRef): JsonObject {
    return when (portRef) {
        is DataSourceRef -> buildJsonObject {
            put("type", "datasource")
            put("dataSourceId", portRef.dataSourceId)
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
    put("src", shader.src)
}

private fun jsonFor(shaderInstance: ShaderInstance) = buildJsonObject {
    put("shaderId", shaderInstance.shaderId)
    put("incomingLinks", shaderInstance.incomingLinks.jsonMap { jsonFor(it) })
    put("shaderChannel", shaderInstance.shaderChannel.id)
    put("priority", shaderInstance.priority)
}

fun Plugins.expectJson(expected: JsonElement, block: () -> JsonElement) {
    val serialModule = serialModule
    val json = Json {
        prettyPrint = true
        serializersModule = serialModule
    }
    fun JsonElement.toStr() = json.encodeToString(JsonElement.serializer(), this)
    kexpect(block().toStr()).toBe(expected.toStr())
}
