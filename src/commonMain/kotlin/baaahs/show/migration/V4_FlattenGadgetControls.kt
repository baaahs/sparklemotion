package baaahs.show.migration

import baaahs.show.DataMigrator
import kotlinx.serialization.json.*

/**
 * Migrate from:
 * ```json
 * {
 *   "type": "baaahs.Core:Gadget",
 *   "gadget": {
 *     "type": "baaahs.Core:Slider",
 *     "title": "Brightness",
 *     "maxValue": 1.25
 *   },
 *   "controlledDataSourceId": "brightnessSlider"
 * }
 * ```
 *
 * to:
 * ```json
 * {
 *   "type": "baaahs.Core:Slider",
 *   "title": "Brightness",
 *   "maxValue": 1.25,
 *   "controlledDataSourceId": "brightnessSlider"
 * }
 * ```
 */
@Suppress("ClassName")
object V4_FlattenGadgetControls : DataMigrator.Migration(4) {
    override fun migrate(from: JsonObject): JsonObject {
        return from.toMutableMap().apply {
            mapObjsInDict("controls") { _, control ->
                val type = control.type
                if (type == "baaahs.Core:Gadget") {
                    val gadgetData = control["gadget"]?.jsonObject
                    control.remove("gadget")!!

                    when (val gadgetType = gadgetData?.get("type")?.jsonPrimitive?.contentOrNull) {
                        "baaahs.Core:Slider" -> {
                            control["type"] = JsonPrimitive(gadgetType)
                            arrayOf("title", "initialValue", "minValue", "maxValue", "stepValue")
                                .forEach { key ->
                                    gadgetData[key]?.let { control[key] = it.jsonPrimitive }
                                }
                        }

                        "baaahs.Core:ColorPicker" -> {
                            control["type"] = JsonPrimitive(gadgetType)
                            arrayOf("title", "initialValue")
                                .forEach { key ->
                                    gadgetData[key]?.let { control[key] = it.jsonPrimitive }
                                }
                        }

                        else -> error("No clue how to migrate $control, sorry!")
                    }
                }
            }
        }.toJsonObj()
    }
}