package baaahs.shows

import baaahs.glshaders.CorePlugin
import baaahs.glshaders.InputPort
import baaahs.glshaders.Plugins
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
                JsonConfiguration.Stable.copy(
                    prettyPrint = true
                )
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

private fun JsonObjectBuilder.addPatchy(patchy: Patchy) {
    "title" to patchy.title
    "patches" to patchy.patches.jsonMap { jsonFor(it) }
    "eventBindings" to patchy.eventBindings.jsonMap { jsonFor(it) }
    "controlLayout" to patchy.controlLayout.jsonMap { it.jsonMap { jsonFor(it) } }
}

private fun <V> Map<String, V>.jsonMap(block: JsonObjectBuilder.(V) -> JsonElement): JsonObject {
    return json { entries.forEach { (k, v) -> k to block(v) } }
}

private fun <T> List<T>.jsonMap(block: (T) -> JsonElement): JsonArray {
    return jsonArray { forEach { +block(it) } }
}

private fun forJson(show: Show): JsonObject {
    return json {
        addPatchy(show)
        "scenes" to show.scenes.jsonMap { jsonFor(it) }
        "layouts" to json {
            "panelNames" to show.layouts.panelNames.jsonMap { JsonPrimitive(it) }
            "map" to show.layouts.map.jsonMap {
                json { "rootNode" to it.rootNode }
            }
        }
        "shaders" to show.shaders.jsonMap { jsonFor(it) }
        "dataSources" to show.dataSources.jsonMap { jsonFor(it) }
    }
}

private fun jsonFor(scene: Scene): JsonObject {
    return json {
        addPatchy(scene)
        "patchSets" to jsonArray {
            for (it in scene.patchSets) {
                +jsonFor(it)
            }
        }
    }
}

fun jsonFor(patchSet: PatchSet): JsonElement {
    return json {
        addPatchy(patchSet)
    }

}

private fun jsonFor(eventBinding: EventBinding) = json { }

fun jsonFor(controlRef: ControlRef): JsonElement {
    return json {
        "type" to controlRef.type.name
        "id" to controlRef.id
    }
}

fun jsonFor(dataSource: DataSource): JsonElement {
    return when (dataSource) {
        is CorePlugin.SliderDataSource -> {
            json {
                "type" to "baaahs.glshaders.CorePlugin.SliderDataSource"
                "title" to dataSource.title
                "initialValue" to dataSource.initialValue
                "minValue" to dataSource.minValue
                "maxValue" to dataSource.maxValue
                "stepValue" to dataSource.stepValue
            }
        }
        is CorePlugin.ColorPickerProvider -> {
            json {
                "type" to "baaahs.glshaders.CorePlugin.ColorPickerProvider"
                "title" to dataSource.title
                "initialValue" to dataSource.initialValue.toInt()
            }
        }
        is CorePlugin.Resolution -> {
            json {
                "type" to "baaahs.glshaders.CorePlugin.Resolution"
            }
        }
        is CorePlugin.Time -> {
            json {
                "type" to "baaahs.glshaders.CorePlugin.Time"
            }
        }
        is CorePlugin.PixelCoordsTexture -> {
            json {
                "type" to "baaahs.glshaders.CorePlugin.PixelCoordsTexture"
            }
        }
        is CorePlugin.ModelInfoDataSource -> {
            json {
                "type" to "baaahs.glshaders.CorePlugin.ModelInfoDataSource"
                "structType" to dataSource.structType
            }
        }
        else -> json { "type" to "unknown" }
    }
}

private fun jsonFor(patch: Patch): JsonObject {
    return json {
        "links" to patch.links.jsonMap { jsonFor(it) }
        "surfaces" to json {
            "name" to "All Surfaces"
        }
    }
}

private fun jsonFor(it: Link): JsonObject {
    return json {
        "from" to jsonFor(it.from)
        "to" to jsonFor(it.to)
    }
}

private fun jsonFor(inputPort: InputPort): JsonObject {
    return json {
        "id" to inputPort.id
        "type" to inputPort.dataType
        "title" to inputPort.title
        "pluginRef" to inputPort.pluginRef
        "pluginConfig" to inputPort.pluginConfig?.jsonMap { it }
        "varName" to inputPort.varName
        "isImplicit" to inputPort.isImplicit
    }
}

private fun jsonFor(portRef: PortRef): JsonObject {
    return when (portRef) {
        is DataSourceRef -> json {
            "type" to "datasource"
            "dataSourceId" to portRef.dataSourceId
        }
        is ShaderInPortRef -> json {
            "type" to "shader-in"
            "shaderId" to portRef.shaderId
            "portId" to portRef.portId
        }
        is ShaderOutPortRef -> json {
            "type" to "shader-out"
            "shaderId" to portRef.shaderId
            "portId" to portRef.portId
        }
        is OutputPortRef -> json {
            "type" to "output"
            "portId" to portRef.portId
        }
        else -> error("huh? $portRef")
    }
}

private fun jsonFor(shader: Shader) = json { "src" to shader.src }

fun expectJson(expected: JsonElement, block: () -> JsonElement) {
    val json = Json(JsonConfiguration.Stable.copy(prettyPrint = true))
    fun JsonElement.toStr() = json.stringify(JsonElementSerializer, this)
    kotlin.test.expect(expected.toStr()) { block().toStr() }
}
