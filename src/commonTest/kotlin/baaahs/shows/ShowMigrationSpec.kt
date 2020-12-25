package baaahs.shows

import baaahs.describe
import baaahs.gl.override
import baaahs.gl.testPlugins
import baaahs.plugin.CorePlugin
import baaahs.show.Show
import baaahs.show.ShowMigrator
import baaahs.toBeSpecified
import baaahs.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import kotlinx.serialization.json.*
import org.spekframework.spek2.Spek

object ShowMigrationSpec : Spek({
    describe<ShowMigrator> {
        val json by value { Json { serializersModule = testPlugins().serialModule } }
        val fromJson by value<JsonObject> { toBeSpecified() }
        val show by value { json.decodeFromJsonElement(ShowMigrator, fromJson) }
        val newJson by value { json.encodeToJsonElement(Show.serializer(), show) }

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
                expect(show.dataSources["modelInfo"]).toBe(CorePlugin.ModelInfoDataSource())
                expect(show.dataSources["time"]).toBe(CorePlugin.TimeDataSource())
            }

            it("permits missing surfaces.deviceTypes") {
                expect(show.patches[0].surfaces.deviceTypes).toBe(emptySet())
            }
        }

        context("from v1") {
            override(fromJson) {
                buildJsonObject {
                    put("version", 1)
                    put("title", "Show")
                    put("shaders", buildJsonObject {
                        put("shader1", buildJsonObject {
                            put("title", "1"); put("type", "Anything"); put("src", "// nothing")
                        })
                    })
                }
            }

            it("drops Shader.type") {
                expect(
                    newJson
                        .jsonObject["shaders"]!!
                        .jsonObject["shader1"]!!
                        .jsonObject
                ).toEqual(
                    buildJsonObject {
                        put("title", JsonPrimitive("1"))
                        put("src", JsonPrimitive("// nothing"))
                    }
                )
            }
        }

        context("when writing") {
            val toJson by value { json.encodeToJsonElement(ShowMigrator, Show("test")) }
            it("includes version") {
                expect(toJson.jsonObject["version"]?.jsonPrimitive?.intOrNull).toBe(2)
            }
        }
    }
})