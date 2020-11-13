package baaahs.shows

import baaahs.describe
import baaahs.gl.override
import baaahs.plugin.CorePlugin
import baaahs.plugin.Plugins
import baaahs.show.Show
import baaahs.show.ShowMigrator
import baaahs.toBeSpecified
import kotlinx.serialization.json.*
import org.spekframework.spek2.Spek
import kotlin.test.expect

object ShowMigrationSpec : Spek({
    describe<ShowMigrator> {
        val json by value { Json { serializersModule = Plugins.safe().serialModule } }
        val fromJson by value<JsonObject> { toBeSpecified() }
        val show by value { json.decodeFromJsonElement(ShowMigrator, fromJson) }

        context("from v0") {
            override(fromJson) {
                buildJsonObject {
                    put("title", "Show")
                    put("dataSources", buildJsonObject {
                        put("modelInfo", buildJsonObject {
                            put("type", "baaahs.plugin.CorePlugin.ModelInfoDataSource")
                            put("structType", "ModelInfo")
                        })
                        put("time", buildJsonObject {
                            put("type", "baaahs.plugin.CorePlugin.Time")
                        })
                    })
                    put("patches", buildJsonArray {
                        add(buildJsonObject {
                            put("shaderInstanceIds", buildJsonArray { })
                            put("surfaces", buildJsonObject {
                                put("name", "All Surfaces")
                            })
                        })
                    })
                }
            }

            it("ignores `structType` and maps from old class names") {
                expect(CorePlugin.ModelInfoDataSource()) { show.dataSources["modelInfo"] }
                expect(CorePlugin.TimeDataSource()) { show.dataSources["time"] }
            }

            it("permits missing surfaces.deviceTypes") {
                expect(emptySet()) { show.patches[0].surfaces.deviceTypes }
            }
        }

        context("when writing") {
            val toJson by value { json.encodeToJsonElement(ShowMigrator, Show("test")) }
            it("includes version") {
                expect(1) { toJson.jsonObject["version"]?.jsonPrimitive?.intOrNull }
            }
        }
    }
})