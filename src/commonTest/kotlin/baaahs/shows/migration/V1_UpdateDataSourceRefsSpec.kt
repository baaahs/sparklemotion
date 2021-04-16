package baaahs.shows.migration

import baaahs.describe
import baaahs.gl.override
import baaahs.gl.testPlugins
import baaahs.plugin.core.datasource.ModelInfoDataSource
import baaahs.plugin.core.datasource.TimeDataSource
import baaahs.show.Show
import baaahs.show.migration.V1_UpdateDataSourceRefs
import baaahs.toBeSpecified
import baaahs.useBetterSpekReporter
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import org.spekframework.spek2.Spek

@Suppress("ClassName")
object V1_UpdateDataSourceRefsSpec : Spek({
    useBetterSpekReporter()

    describe<V1_UpdateDataSourceRefs> {
        val migration by value { V1_UpdateDataSourceRefs }
        val json by value { Json { serializersModule = testPlugins().serialModule } }
        val fromJson by value<String> { toBeSpecified() }
        val fromJsonObj by value { json.parseToJsonElement(fromJson) as JsonObject }
        val toJsonObj by value { migration.migrate(fromJsonObj) }
        val show by value { json.decodeFromJsonElement(Show.serializer(), toJsonObj)}

        context("migration of layouts") {
            override(fromJson) {
                /**language=json*/
                """
                {
                  "title": "Show",
                  "dataSources": {
                    "modelInfo": {
                      "type": "baaahs.plugin.CorePlugin.ModelInfoDataSource",
                      "structType": "ModelInfo"
                    },
                    "time": {
                      "type": "baaahs.plugin.CorePlugin.Time"
                    }
                  },
                  "patches": [
                    {
                      "shaderInstanceIds": [],
                      "surfaces": {
                        "name": "All Surfaces"
                      }
                    }
                  ]
                }
                """.trimIndent()
            }

            it("ignores `structType` and maps from old class names") {
                expect(show.dataSources["modelInfo"]).toBe(ModelInfoDataSource())
                expect(show.dataSources["time"]).toBe(TimeDataSource())
            }

            it("permits missing surfaces.deviceTypes") {
                expect(show.patches[0].surfaces.deviceTypes).toBe(emptySet())
            }
        }
    }
})