package baaahs.show

import baaahs.gl.patch.ContentType
import kotlinx.serialization.json.*

object ShowMigrator : JsonTransformingSerializer<Show>(Show.serializer()) {
    private const val currentVersion = 2
    private const val versionKey = "version"

    override fun transformDeserialize(element: JsonElement): JsonElement {
        if (element !is JsonObject) return element
        val fromVersion = element[versionKey]?.jsonPrimitive?.int ?: 0
        if (fromVersion == currentVersion)
            return element.toMutableMap()
                .apply { remove(versionKey) }.toJsonObj()

        val newJson = element.toMutableMap()
        newJson.remove(versionKey)

        if (fromVersion < 1) {
            FromV0.apply(newJson)
        }

        if (fromVersion < 2) {
            FromV1.apply(newJson)
        }

        return newJson.toJsonObj()
    }

    override fun transformSerialize(element: JsonElement): JsonElement {
        if (element !is JsonObject) return element
        return element.toMutableMap().apply {
            put(versionKey, JsonPrimitive(currentVersion))
        }.toJsonObj()
    }

    private fun Map<String, JsonElement>.toJsonObj(): JsonObject = buildJsonObject {
        forEach { (k, v) -> put(k, v) }
    }

    fun MutableMap<String, JsonElement>.mapObjsInDict(
        key: String,
        callback: (id: String, item: MutableMap<String, JsonElement>) -> Unit
    ) {
        (this[key] as JsonObject?)?.mapValues { (id, obj) ->
            val objMap = (obj as JsonObject).toMutableMap()
            callback(id, objMap)
            objMap.toJsonObj()
        }?.also { this[key] = it.toJsonObj() }
    }

    object FromV0 {
        private val dataSourceTypeMap = mapOf(
            "baaahs.plugin.CorePlugin.Resolution" to "baaahs.Core:Resolution",
            "baaahs.plugin.CorePlugin.PreviewResolution" to "baaahs.Core:PreviewResolution",
            "baaahs.plugin.CorePlugin.Time" to "baaahs.Core:Time",
            "baaahs.plugin.CorePlugin.PixelCoordsTexture" to "baaahs.Core:PixelCoordsTexture",
            "baaahs.plugin.CorePlugin.ScreenUvCoord" to "baaahs.Core:ScreenUvCoord",
            "baaahs.plugin.CorePlugin.ModelInfoDataSource" to "baaahs.Core:ModelInfo",
            "baaahs.plugin.CorePlugin.SliderDataSource" to "baaahs.Core:Slider",
            "baaahs.plugin.CorePlugin.XyPad" to "baaahs.Core:XyPad",
            "baaahs.plugin.CorePlugin.ColorPicker" to "baaahs.Core:ColorPicker",
            "baaahs.plugin.CorePlugin.RadioButtonStripProvider" to "baaahs.Core:RadioButtonStrip",
            "baaahs.plugin.CorePlugin.ImageSource" to "baaahs.Core:Image"
        )

        fun apply(newJson: MutableMap<String, JsonElement>) {
            newJson.mapObjsInDict("dataSources") { _, dataSource ->
                val type = dataSource["type"]?.jsonPrimitive?.contentOrNull
                if (type == "baaahs.plugin.CorePlugin.ModelInfoDataSource") {
                    dataSource.remove("structType")
                }

                if (type != null) {
                    dataSourceTypeMap[type]?.let { dataSource["type"] = JsonPrimitive(it) }
                }
            }
        }
    }

    object FromV1 {
        private val shaderTypeToPrototypeMap = mapOf(
            "Projection" to "baaahs.Core:Projection",
            "Distortion" to "baaahs.Core:Distortion",
            "Filter" to "baaahs.Core:Filter",
            "Mover" to "baaahs.Core:Mover",
            "Unknown" to null
        )

        private val shaderTypeToResultContentTypeMap = mapOf(
            "Projection" to ContentType.UvCoordinate,
            "Distortion" to ContentType.UvCoordinate,
            "Paint" to ContentType.Color,
            "Filter" to ContentType.Color,
            "Mover" to ContentType.PanAndTilt,
            "Unknown" to ContentType.Unknown
        )

        fun apply(newJson: MutableMap<String, JsonElement>) {
            newJson.mapObjsInDict("shaders") { _, shader ->
                val type = shader.remove("type")?.jsonPrimitive?.contentOrNull

                val prototype = if (type == "Paint") {
                    val src = shader["src"]?.jsonPrimitive?.contentOrNull
                    if (src?.contains("mainImage") == true) {
                        "baaahs.Core:Paint/ShaderToy"
                    } else {
                        "baaahs.Core:Paint"
                    }
                } else shaderTypeToPrototypeMap[type]

                val resultContentType = shaderTypeToResultContentTypeMap[type]
                    ?: error("Unknown shader type \"$type\"")

                shader["prototype"] = prototype?.let { buildJsonObject { put("type", prototype) } }
                    ?: JsonNull
                shader["resultContentType"] = JsonPrimitive(resultContentType.id)
            }
        }
    }
}