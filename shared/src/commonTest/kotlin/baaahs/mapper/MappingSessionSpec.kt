package baaahs.mapper

import baaahs.*
import baaahs.geom.Matrix4F
import baaahs.geom.Vector3F
import baaahs.geom.identity
import baaahs.kotest.value
import baaahs.show.SampleData
import baaahs.sim.FakeFs
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json

class MappingSessionSpec : DescribeSpec({
    describe<MappingSession> {
        context("deserialization") {
            val plugins by value { SampleData.plugins }
            val jsonPrettyPrint by value { Json(plugins.json) { prettyPrint = true } }
            val json by value {
                /**language=json*/
                """
                    {
                      "startedAt": 12345600000,
                      "surfaces": [
                        {
                          "brainId": "brain1234",
                          "panelName": "panel1234",
                          "pixels": [{"modelPosition": {"x": 1, "y": 2, "z": 3}}]
                        }
                      ],
                      "cameraMatrix": {
                        "elements": [1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1] 
                      },
                      "savedAt": 12345600000
                    }
                """.trimIndent()
            }

            context("Session.loadMappingSession") {
                val fs by value { FakeFs() }
                val decoded by value {
                    val file = fs.resolve("mapping-session.json")
                    doRunBlocking { fs.saveFile(file, json) }
                    doRunBlocking { MappingStore(fs.rootFile, plugins, FakeClock()).loadMappingSession(file) }
                }

                it("deserializes equally") {
                    decoded.startedAt shouldBe(Instant.fromEpochMilliseconds(12345600_000))

                    val surfaces = decoded.surfaces.only("surface")
                    surfaces
                        surfaces.brainId shouldBe("brain1234")
                        surfaces.panelName shouldBe("panel1234")
                        surfaces.pixels shouldBe(listOf(
                            MappingSession.SurfaceData.PixelData(Vector3F(1f, 2f, 3f))
                        ))

                    decoded.cameraMatrix shouldBe(Matrix4F.Companion.identity)
                }
            }

            context("toJson") { }
        }
    }
})