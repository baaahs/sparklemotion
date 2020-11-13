package baaahs.show

import kotlinx.serialization.json.*

object ShowMigrator : JsonTransformingSerializer<Show>(Show.serializer()) {
    private const val currentVersion = 1
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
            newJson["dataSources"] = (newJson["dataSources"] as JsonObject).mapValues { (_, v) ->
                val dataSource = (v as JsonObject).toMutableMap()

                val type = dataSource["type"]?.jsonPrimitive?.contentOrNull
                if (type == "baaahs.plugin.CorePlugin.ModelInfoDataSource") {
                    dataSource.remove("structType")
                }

                if (type != null) {
                    dataSourceTypeMap[type]?.let { dataSource["type"] = JsonPrimitive(it) }
                }

                dataSource.toJsonObj()
            }.toJsonObj()
        }
    }
}