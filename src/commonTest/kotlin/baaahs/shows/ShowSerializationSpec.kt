package baaahs.shows

import baaahs.glshaders.CorePlugin
import baaahs.glshaders.InputPort
import baaahs.glshaders.Plugins
import baaahs.ports.*
import baaahs.show.*
import kotlinx.serialization.json.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

@Suppress("unused")
object ShowSerializationSpec : Spek({
    describe("Show serialization") {
        val plugins by value { Plugins.safe() }
        val jsonPrettyPrint by value {
            Json(JsonConfiguration.Stable.copy(
                prettyPrint = true
            ))
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

private fun forJson(show: Show): JsonObject {
    return json {
        "title" to show.title
        "scenes" to jsonArray {
            show.scenes.forEach { +jsonFor(it) }
        }
        "eventBindings" to jsonArray { show.eventBindings.forEach { +jsonFor(it) } }
        "dataSources" to jsonArray { show.dataSources.forEach { +jsonFor(it) } }
        "layouts" to json {
            "panelNames" to jsonArray {
                show.layouts.panelNames.forEach { +it }
            }
            "map" to json {
                show.layouts.map.entries.forEach { (k, v) ->
                    k to json {
                        "rootNode" to v.rootNode
                    }
                }
            }
        }
        "controlLayout" to jsonFor(show.controlLayout)
        "shaderFragments" to json {
            show.shaderFragments.entries.forEach { (k, v) -> k to v }
        }
    }
}

private fun jsonFor(scene: Scene): JsonObject {
    return json {
        "title" to scene.title
        "patchSets" to jsonArray {
            for (it in scene.patchSets) {
                +jsonFor(it)
            }
        }
        "eventBindings" to jsonArray { scene.eventBindings.forEach { +jsonFor(it) } }
        "controlLayout" to jsonFor(scene.controlLayout)
    }
}

fun jsonFor(patchSet: PatchSet): JsonElement {
    return json {
        "title" to patchSet.title
        "patchMappings" to jsonArray {
            patchSet.patchMappings.forEach {
                +jsonFor(it)
            }
        }
        "eventBindings" to jsonArray { patchSet.eventBindings.forEach { +jsonFor(it) } }
        "controlLayout" to jsonFor(patchSet.controlLayout)
    }

}

private fun jsonFor(eventBinding: EventBinding) = json { }

fun jsonFor(controlLayout: Map<String, List<DataSourceRef>>): JsonObject {
    return json {
        controlLayout.forEach { (k, v) ->
            k to jsonArray { v.forEach { +json { "id" to it.id } } }
        }
    }
}

fun jsonFor(dataSource: DataSource): JsonElement {
    return when (dataSource) {
        is CorePlugin.SliderDataSource -> {
            json {
                "type" to "baaahs.glshaders.CorePlugin.SliderDataSource"
                "id" to dataSource.id
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
                "id" to dataSource.id
                "title" to dataSource.title
                "initialValue" to dataSource.initialValue.toInt()
            }
        }
        is CorePlugin.Scenes -> {
            json {
                "type" to "baaahs.glshaders.CorePlugin.Scenes"
                "id" to dataSource.id
                "title" to dataSource.title
            }
        }
        is CorePlugin.Patches -> {
            json {
                "type" to "baaahs.glshaders.CorePlugin.Patches"
                "id" to dataSource.id
                "title" to dataSource.title
            }
        }
        is CorePlugin.Resolution -> {
            json {
                "type" to "baaahs.glshaders.CorePlugin.Resolution"
                "id" to dataSource.id
            }
        }
        is CorePlugin.Time -> {
            json {
                "type" to "baaahs.glshaders.CorePlugin.Time"
                "id" to dataSource.id
            }
        }
        is CorePlugin.UvCoordTexture -> {
            json {
                "type" to "baaahs.glshaders.CorePlugin.UvCoordTexture"
                "id" to dataSource.id
            }
        }
        else -> json { "type" to "unknown" }
    }
}

private fun jsonFor(patchMapping: PatchMapping): JsonObject {
    return json {
        "links" to jsonArray {
            patchMapping.links.forEach {
                +jsonFor(it)
            }
        }
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
        "pluginConfig" to inputPort.pluginConfig?.forEach { (k, v) -> k to v }
        "varName" to inputPort.varName
        "isImplicit" to inputPort.isImplicit
    }
}

private fun jsonFor(portRef: PortRef): JsonObject {
    return when (portRef) {
        is DataSourceRef -> json {
            "type" to "baaahs.ports.DataSourceRef"
            "id" to portRef.id
        }
        is ShaderInPortRef -> json {
            "type" to "baaahs.ports.ShaderInPortRef"
            "shaderId" to portRef.shaderId
            "portName" to portRef.portName
        }
        is ShaderOutPortRef -> json {
            "type" to "baaahs.ports.ShaderOutPortRef"
            "shaderId" to portRef.shaderId
        }
        is OutputPortRef -> json {
            "type" to "baaahs.ports.OutputPortRef"
        }
        else -> error("huh? $portRef")
    }
}

fun expectJson(expected: JsonElement, block: () -> JsonElement) {
    val json = Json(JsonConfiguration.Stable.copy(prettyPrint = true))
    fun JsonElement.toStr() = json.stringify(JsonElementSerializer, this)
    kotlin.test.expect(expected.toStr()) { block().toStr() }
}
