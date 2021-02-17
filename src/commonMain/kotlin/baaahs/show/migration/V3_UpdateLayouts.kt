package baaahs.show.migration

import baaahs.show.ShowMigrator
import kotlinx.serialization.json.*

@Suppress("ClassName")
object V3_UpdateLayouts : ShowMigrator.Migration(2) {
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
                    put("panelNames", buildJsonArray {
                        defaultPanels.forEach { add(it) }
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
                                        defaultPanels.forEach { add(it) }
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

    private fun replaceUnknownPanelNames(controlJson: MutableMap<String, JsonElement>) {
        controlJson.replaceJsonObj("controlLayout") { controlLayout ->
            controlLayout.toMutableMap().apply {
                forEach { (panel, contents) ->
                    if (!defaultPanels.contains(panel)) {
                        remove(panel)
                        this["Controls"] = contents
                    }
                }
            }.toJsonObj()
        }
    }
}