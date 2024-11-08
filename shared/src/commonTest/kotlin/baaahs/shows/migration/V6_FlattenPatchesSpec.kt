package baaahs.shows.migration

import baaahs.describe
import baaahs.gl.override
import baaahs.gl.testPlugins
import baaahs.kotest.value
import baaahs.show.migration.V6_FlattenPatches
import baaahs.toBeSpecified
import baaahs.toEqual
import baaahs.useBetterSpekReporter
import ch.tutteli.atrium.api.verbs.expect
import io.kotest.core.spec.style.DescribeSpec
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

@Suppress("ClassName")
object V6_FlattenPatchesSpec : DescribeSpec({
    useBetterSpekReporter()

    describe<V6_FlattenPatches> {
        val migration by value { V6_FlattenPatches }
        val json by value { Json { serializersModule = testPlugins().serialModule } }
        val fromJson by value<String> { toBeSpecified() }
        val fromJsonObj by value { json.parseToJsonElement(fromJson) as JsonObject }
        val toJsonObj by value { migration.migrate(fromJsonObj) }

        context("migration of patches and shader instances") {
            override(fromJson) {
                /**language=json*/
                """
                    {
                      "title": "Show",
                      "patches": [
                        {
                          "shaderInstanceIds": ["a"],
                          "surfaces": {"name": "All Surfaces"}
                        }
                      ],
                      "controls": {
                        "aControl": {
                          "patches": [
                            {
                              "shaderInstanceIds": ["b"],
                              "surfaces": {"name": "All Surfaces"}
                            },
                            {
                              "shaderInstanceIds": ["c"],
                              "surfaces": {"name": "All Surfaces"}
                            }
                          ]
                        }
                      },
                      "shaderInstances": {
                        "a": {},
                        "b": {},
                        "c": {}
                      }
                    }
                """.trimIndent()
            }

            it("merges them together into patches") {
                expect(toJsonObj).toEqual(json.parseToJsonElement(
                    /**language=json*/
                    """
                        {
                          "title": "Show",
                          "patchIds": ["a"],
                          "controls": {
                            "aControl": {
                              "patchIds": ["b", "c"]
                            }
                          },
                          "patches": {
                            "a": {},
                            "b": {},
                            "c": {}
                          }
                        }
                    """.trimIndent()
                ).jsonObject)
            }
        }
    }
})