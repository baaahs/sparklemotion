package baaahs.shows.migration

import baaahs.describe
import baaahs.gl.override
import baaahs.gl.testPlugins
import baaahs.kotest.value
import baaahs.show.migration.V4_FlattenGadgetControls
import baaahs.toBeSpecified
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.core.spec.style.DescribeSpec
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Suppress("ClassName")
class V4_FlattenGadgetControlsSpec : DescribeSpec({
    describe<V4_FlattenGadgetControls> {
        val migration by value { V4_FlattenGadgetControls }
        val json by value { Json { serializersModule = testPlugins().serialModule } }
        val fromJson by value<String> { toBeSpecified() }
        val fromJsonObj by value { json.parseToJsonElement(fromJson) as JsonObject }
        val toJsonObj by value { migration.migrate(fromJsonObj) }
        val toJsonStr by value { json.encodeToString(JsonElement.serializer(), toJsonObj) }

        context("migration of gadget controls") {
            override(fromJson) {
                /**language=json*/
                """
                    {
                      "controls": {
                        "someControl": {
                          "type": "baaahs.Core:Gadget",
                          "gadget": {
                            "type": "baaahs.Core:Slider",
                            "title": "Brightness",
                            "maxValue": 1.25
                          },
                          "controlledDataSourceId": "brightnessSlider"
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
                        "controls": {
                          "someControl": {
                            "type": "baaahs.Core:Slider",
                            "title": "Brightness",
                            "maxValue": 1.25,
                            "controlledDataSourceId": "brightnessSlider"
                          }
                        }
                      }
                    """.trimIndent()
                )
            }
        }
    }
})