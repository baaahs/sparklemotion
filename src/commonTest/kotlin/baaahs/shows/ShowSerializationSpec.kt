package baaahs.shows

import baaahs.glshaders.CorePlugin
import baaahs.glshaders.Plugins
import baaahs.show.DataSource
import baaahs.ports.*
import baaahs.show.*
import kotlinx.serialization.json.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.expect

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
        "patchSets" to jsonArray { }
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

fun jsonFor(controlLayout: Map<String, List<DataSource>>): JsonObject {
    return json {
        controlLayout.forEach { (k, v) ->
            k to jsonArray { v.forEach { +jsonFor(it) } }
        }
    }
}

fun jsonFor(dataSource: DataSource): JsonElement {
    return when (dataSource) {
        is CorePlugin.SliderProvider -> {
            json {
                "#type" to "baaahs.glshaders.CorePlugin.SliderProvider"
                "id" to dataSource.id
                "inputPortRef" to jsonFor(dataSource.inputPortRef)
                "supportedTypes" to jsonArray {
                    dataSource.supportedTypes.forEach { +it }
                }
            }
        }
        is CorePlugin.ColorPickerProvider -> {
            json {
                "#type" to "baaahs.glshaders.CorePlugin.ColorPickerProvider"
                "id" to dataSource.id
                "inputPortRef" to jsonFor(dataSource.inputPortRef)
                "supportedTypes" to jsonArray {
                    dataSource.supportedTypes.forEach { +it }
                }
            }
        }
        is CorePlugin.Scenes -> {
            json {
                "#type" to "baaahs.glshaders.CorePlugin.Scenes"
                "id" to dataSource.id
                "inputPortRef" to jsonFor(dataSource.inputPortRef)
                "supportedTypes" to jsonArray {
                    dataSource.supportedTypes.forEach { +it }
                }
            }
        }
        is CorePlugin.Patches -> {
            json {
                "#type" to "baaahs.glshaders.CorePlugin.Patches"
                "id" to dataSource.id
                "inputPortRef" to jsonFor(dataSource.inputPortRef)
                "supportedTypes" to jsonArray {
                    dataSource.supportedTypes.forEach { +it }
                }
            }
        }
        is CorePlugin.Resolution -> {
            json {
                "#type" to "baaahs.glshaders.CorePlugin.Resolution"
                "id" to dataSource.id
                "supportedTypes" to jsonArray {
                    dataSource.supportedTypes.forEach { +it }
                }
            }
        }
        is CorePlugin.Time -> {
            json {
                "#type" to "baaahs.glshaders.CorePlugin.Time"
                "id" to dataSource.id
                "supportedTypes" to jsonArray {
                    dataSource.supportedTypes.forEach { +it }
                }
            }
        }
        is CorePlugin.UvCoord -> {
            json {
                "#type" to "baaahs.glshaders.CorePlugin.UvCoord"
                "id" to dataSource.id
                "supportedTypes" to jsonArray {
                    dataSource.supportedTypes.forEach { +it }
                }
            }
        }
        else -> json { "#type" to "unknown" }
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

private fun jsonFor(inputPortRef: InputPortRef): JsonObject {
    return json {
        "id" to inputPortRef.id
        "type" to inputPortRef.type
        "title" to inputPortRef.title
        "pluginId" to inputPortRef.pluginId
        "pluginConfig" to json { inputPortRef.pluginConfig.forEach { (k, v) -> k to v } }
        "varName" to inputPortRef.varName
        "isImplicit" to inputPortRef.isImplicit
    }
}

private fun jsonFor(portRef: PortRef): JsonObject {
    return when (portRef) {
        is InputPortRef -> json {
            "#type" to "baaahs.ports.InputPortRef"
            "id" to portRef.id
            "type" to portRef.type
            "title" to portRef.title
            "pluginId" to portRef.pluginId
            "pluginConfig" to json { portRef.pluginConfig.forEach { (k, v) -> k to v } }
            "varName" to portRef.varName
            "isImplicit" to portRef.isImplicit
        }
        is ShaderInPortRef -> json {
            "#type" to "baaahs.ports.ShaderInPortRef"
            "shaderId" to portRef.shaderId
            "portName" to portRef.portName
        }
        is ShaderOutPortRef -> json {
            "#type" to "baaahs.ports.ShaderOutPortRef"
            "shaderId" to portRef.shaderId
        }
        is OutputPortRef -> json {
            "#type" to "baaahs.ports.OutputPortRef"
        }
        else -> error("huh? $portRef")
    }
}

expect fun expectJson(expected: JsonElement, block: () -> JsonElement)
