package baaahs.shows.migration

import baaahs.describe
import baaahs.gl.override
import baaahs.gl.testPlugins
import baaahs.show.migration.V3_UpdateLayouts
import baaahs.toBeSpecified
import baaahs.toEqualJson
import baaahs.useBetterSpekReporter
import ch.tutteli.atrium.api.verbs.expect
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import org.spekframework.spek2.Spek

@Suppress("ClassName")
object V3_UpdateLayoutsSpec : Spek({
    useBetterSpekReporter()

    describe<V3_UpdateLayouts> {
        val migration by value { V3_UpdateLayouts }
        val json by value { Json { serializersModule = testPlugins().serialModule } }
        val fromJson by value<String> { toBeSpecified() }
        val fromJsonObj by value { json.parseToJsonElement(fromJson) as JsonObject }
        val toJsonObj by value { migration.migrate(fromJsonObj) }

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
                          "controlLayout": { "Something Else": ["another"] }
                        }
                      }
                    }
                """.trimIndent()
            }

            it("replaces any old layout with new sample layout") {
                expect(toJsonObj).toEqualJson(
                    /**language=json*/
                    """
                      {
                        "layouts": {
                          "panelNames": [
                            "Scenes",
                            "Preview",
                            "Backdrops",
                            "Controls",
                            "More Controls",
                            "Transition"
                          ],
                          "formats": {
                            "default": {
                              "mediaQuery": null,
                              "tabs": [
                                {
                                  "title": "Main",
                                  "columns": [ "3fr", "2fr" ],
                                  "rows": [ "2fr", "5fr", "3fr" ],
                                  "areas": [
                                    "Scenes", "Preview",
                                    "Backdrops", "Controls",
                                    "More Controls", "Transition"
                                  ]
                                }
                              ]
                            }
                          }
                        },
                        "controlLayout": { "Controls": "whatever" },
                        "controls": {
                          "someControl": {
                            "controlLayout": { "Controls": ["another"] }
                          }
                        }

                      }
                    """.trimIndent()
                )
            }
        }
    }
})