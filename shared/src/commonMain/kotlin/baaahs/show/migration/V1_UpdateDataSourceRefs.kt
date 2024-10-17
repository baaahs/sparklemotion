package baaahs.show.migration

import baaahs.migrator.DataMigrator
import baaahs.migrator.mapObjsInDict
import baaahs.migrator.toJsonObj
import baaahs.migrator.type
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

@Suppress("ClassName")
object V1_UpdateDataSourceRefs : DataMigrator.Migration(1) {
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

    override fun migrate(from: JsonObject): JsonObject {
        return from.toMutableMap().apply {
            mapObjsInDict("dataSources") { _, dataSource ->
                val type = dataSource.type
                if (type == "baaahs.plugin.CorePlugin.ModelInfoDataSource") {
                    dataSource.remove("structType")
                }

                if (type != null) {
                    dataSourceTypeMap[type]?.let { dataSource["type"] = JsonPrimitive(it) }
                }
            }
        }.toJsonObj()
    }
}