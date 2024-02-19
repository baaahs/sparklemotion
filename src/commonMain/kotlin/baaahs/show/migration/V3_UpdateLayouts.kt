package baaahs.show.migration

import baaahs.camelize
import baaahs.migrator.DataMigrator
import baaahs.migrator.mapObjsInDict
import baaahs.migrator.replaceJsonObj
import baaahs.migrator.toJsonObj
import kotlinx.serialization.json.*

@Suppress("ClassName")
object V3_UpdateLayouts : DataMigrator.Migration(3) {
    private val defaultPanels = listOf(
        "Scenes",
        "Preview",
        "Backdrops",
        "Controls",
        "More Controls",
        "Transition"
    )

    override fun migrate(from: JsonObject): JsonObject {
        // Since probably nobody's created a custom layout yet, we'll
        // just ignore what's there and drop in a pre-baked default.
        return from.toMutableMap().apply {
            replaceJsonObj("layouts") {
                buildJsonObject {
                    put("panels", buildJsonObject {
                        defaultPanels.forEach {
                            put(it.suggestId(), buildJsonObject { put("title", it) })
                        }
                    })

                    put("formats", buildJsonObject {
                        put("default", buildJsonObject {
                            put("mediaQuery", JsonNull)
                            put("tabs", buildJsonArray {
                                add(buildJsonObject {
                                    put("title", "Main")
                                    put("columns", buildJsonArray { add("3fr"); add("2fr") })
                                    put("rows", buildJsonArray { add("2fr"); add("5fr"); add("3fr") })
                                    put("areas", buildJsonArray {
                                        defaultPanels.forEach { add(it.suggestId()) }
                                    })
                                })
                            })
                        })
                    })
                }
            }

            // For the show:
            replaceUnknownPanelNames(this)

            // For other controls:
            mapObjsInDict("controls") { _, controlJson ->
                replaceUnknownPanelNames(controlJson)
            }
        }.toJsonObj()
    }

    private fun replaceUnknownPanelNames(patchHolder: MutableMap<String, JsonElement>) {
        if (patchHolder.containsKey("controlLayout")) {
            patchHolder.replaceJsonObj("controlLayout") { controlLayout ->
                controlLayout.toMutableMap().apply {
                    keys.toList().forEach { panelName ->
                        if (defaultPanels.contains(panelName)) {
                            this[panelName.suggestId()] = remove(panelName)!!
                        } else {
                            this["controls"] = remove(panelName)!!
                        }
                    }
                }.toJsonObj()
            }
        }
    }

    private fun String.suggestId(): String = camelize()
}