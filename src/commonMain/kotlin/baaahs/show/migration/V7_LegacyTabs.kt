package baaahs.show.migration

import baaahs.migrator.*
import kotlinx.serialization.json.*

/**
 * Tabs are polymorphic. Old ones are LegacyTabs.
 */
@Suppress("ClassName")
object V7_LegacyTabs : DataMigrator.Migration(7) {
    override fun migrate(from: JsonObject): JsonObject {
        return from.toMutableMap().apply {
            replaceJsonObj("layouts") { layouts ->
                JsonObject(layouts.toMutableMap().apply {
                    replaceJsonObj("formats") { formats ->
                        JsonObject(formats.mapValues { (_, format) ->
                            JsonObject(format.jsonObject.toMutableMap().apply {
                                this["tabs"]?.let { tabs ->
                                    this["tabs"] = JsonArray(tabs.jsonArray.map { tab ->
                                        tab.jsonObject.edit {
                                            if (!containsKey("type")) {
                                                put("type", JsonPrimitive("Legacy"))
                                            }
                                        }
                                    })
                                }
                            })
                        })
                    }
                })
            }

            mapObjsInDict("layouts") { name, layout ->
                if (name == "formats") {
                    println("layout = ${layout}")

                    layout.mapObjsInDict("formats") { _, format ->
                        println("format = ${format}")
                        format.mapObjsInArray("tabs") { tab ->
                            println("tab = ${tab}")
                            tab["type"] = JsonPrimitive("Legacy")
                        }
                    }
                }
            }
        }.toJsonObj().also {
            println("migrated = $it")
        }
    }
}