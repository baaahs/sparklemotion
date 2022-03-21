package baaahs.mapper

import baaahs.describe
import baaahs.geom.Vector3F
import baaahs.only
import baaahs.show.SampleData
import baaahs.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.its
import ch.tutteli.atrium.api.verbs.expect
import kotlinx.serialization.json.Json
import org.spekframework.spek2.Spek

class MappingSessionSpec : Spek({
    describe<MappingSession> {
        context("deserialization") {
            val plugins by value { SampleData.plugins }
            val jsonPrettyPrint by value { Json(plugins.json) { prettyPrint = true } }
            val json by value {
                /**language=json*/
                """
                    {
                      "startedAt": 1234.56,
                      "surfaces": [
                        {
                          "brainId": "brain1234",
                          "panelName": "panel1234",
                          "pixels": [{"modelPosition": {"x": 1, "y": 2, "z": 3}}]
                        }
                      ]
                    }
                """.trimIndent()
            }

            context("fromJson") {
                val decoded by value { Json.decodeFromString(MappingSession.serializer(), json) }

                it("deserializes equally") {
                    expect(decoded)
                        .its({ startedAt }) { toEqual(1234.56) }

                    val surfaces = decoded.surfaces.only("surface")
                    expect(surfaces)
                        .its({ brainId }) { toEqual("brain1234") }
                        .its({ panelName }) { toEqual("panel1234") }
                        .its({ pixels }) { toEqual(listOf(
                            MappingSession.SurfaceData.PixelData(Vector3F(1f, 2f, 3f))
                        )) }
                }
            }

            context("toJson") { }
        }
    }
})