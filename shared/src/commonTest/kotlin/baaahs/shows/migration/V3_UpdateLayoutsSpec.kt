package baaahs.shows.migration

import baaahs.describe
import baaahs.gl.override
import baaahs.gl.testPlugins
import baaahs.kotest.value
import baaahs.show.migration.V3_UpdateLayouts
import baaahs.toBeSpecified
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.core.spec.style.DescribeSpec
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Suppress("ClassName")
object V3_UpdateLayoutsSpec : DescribeSpec({
    describe<V3_UpdateLayouts> {
        val migration by value { V3_UpdateLayouts }
        val json by value { Json { serializersModule = testPlugins().serialModule } }
        val fromJson by value<String> { toBeSpecified() }
        val fromJsonObj by value { json.parseToJsonElement(fromJson) as JsonObject }
        val toJsonObj by value { migration.migrate(fromJsonObj) }
        val toJsonStr by value { json.encodeToString(JsonElement.serializer(), toJsonObj) }

        context("migration of layouts") {
            override(fromJson) {
                /**language=json*/
                """
                    {
                      "layouts": {
                        "panelNames": [
                          "Scenes",
                          "Backdrops",
                          "More Controls",
                          "Preview",
                          "Controls"
                        ],
                        "map": {
                          "default": {
                            "rootNode": {
                              "direction": "row",
                              "splitPercentage": 70,
                              "first": {
                                "direction": "column",
                                "splitPercentage": 20,
                                "first": "Scenes",
                                "second": {
                                  "direction": "column",
                                  "splitPercentage": 60,
                                  "first": "Backdrops",
                                  "second": "More Controls"
                                }
                              },
                              "second": {
                                "direction": "column",
                                "splitPercentage": 20,
                                "first": "Preview",
                                "second": "Controls"
                              }
                            }
                          }
                        }
                      },
                      "controlLayout": { "Effects": "whatever" },
                      "controls": {
                        "someControl": {
                          "controlLayout": {
                            "More Controls": ["another"],
                            "Even More Controls": ["stillAnother"]
                          }
                        }
                      }
                    }
                """.trimIndent()
            }

            it("replaces any old layout with new sample layout") {
                toJsonStr.shouldEqualJson(
                    /**language=json*/
                    """
                      {
                        "layouts": {
                          "panels": {
                            "scenes": {"title": "Scenes"},
                            "preview": {"title": "Preview"},
                            "backdrops": {"title": "Backdrops"},
                            "controls": {"title": "Controls"},
                            "moreControls": {"title": "More Controls"},
                            "transition": {"title":"Transition"}
                          },
                          "formats": {
                            "default": {
                              "mediaQuery": null,
                              "tabs": [
                                {
                                  "title": "Main",
                                  "columns": [ "3fr", "2fr" ],
                                  "rows": [ "2fr", "5fr", "3fr" ],
                                  "areas": [
                                    "scenes", "preview",
                                    "backdrops", "controls",
                                    "moreControls", "transition"
                                  ]
                                }
                              ]
                            }
                          }
                        },
                        "controlLayout": { "controls": "whatever" },
                        "controls": {
                          "someControl": {
                            "controlLayout": {
                              "moreControls": ["another"],
                              "controls": ["stillAnother"]
                            }
                          }
                        }

                      }
                    """.trimIndent()
                )
            }
        }
    }
})