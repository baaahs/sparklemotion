package baaahs.shows

import baaahs.gl.override
import baaahs.plugin.CorePlugin
import baaahs.plugin.Plugins
import baaahs.show.Show
import baaahs.show.ShowMigrator
import baaahs.toBeSpecified
import describe
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.json
import org.spekframework.spek2.Spek
import kotlin.test.expect

object ShowMigrationSpec : Spek({
    describe<ShowMigrator> {
        val json by value { Json(JsonConfiguration.Stable, Plugins.safe().serialModule) }
        val fromJson by value<JsonObject> { toBeSpecified() }
        val show by value { json.fromJson(ShowMigrator, fromJson) }

        context("from v0") {
            override(fromJson) {
                json {
                    "title" to ""
                    "dataSources" to json {
                        "modelInfo" to json {
                            "type" to "baaahs.plugin.CorePlugin.ModelInfoDataSource"
                            "structType" to "ModelInfo"
                        }
                        "time" to json {
                            "type" to "baaahs.plugin.CorePlugin.Time"
                        }
                    }
                }
            }

            it("ignores `structType` and maps from old class names") {
                expect(CorePlugin.ModelInfoDataSource()) { show.dataSources["modelInfo"] }
                expect(CorePlugin.TimeDataSource()) { show.dataSources["time"] }
            }
        }

        context("when writing") {
            val toJson by value { json.toJson(ShowMigrator, Show("test")) }
            it("includes version") {
                expect(1) { toJson.jsonObject.getPrimitiveOrNull("version")?.intOrNull }
            }
        }
    }
})