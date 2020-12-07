package baaahs.shows

import baaahs.describe
import baaahs.gl.override
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.*
import baaahs.gl.testPlugins
import baaahs.plugin.CorePlugin
import baaahs.show.Show
import baaahs.show.ShowMigrator
import baaahs.toBeSpecified
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import kotlinx.serialization.json.*
import org.spekframework.spek2.Spek

object ShowMigrationSpec : Spek({
    describe<ShowMigrator> {
        val json by value { Json { serializersModule = testPlugins().serialModule } }
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
                        put("proj", buildJsonObject {
                            put("title", "1"); put("type", "Projection"); put("src", "// nothing")
                        })
                        put("dist", buildJsonObject {
                            put("title", "1"); put("type", "Distortion"); put("src", "// nothing")
                        })
                        put("genericPaint", buildJsonObject {
                            put("title", "1"); put("type", "Paint"); put("src", "// nothing")
                        })
                        put("toyPaint", buildJsonObject {
                            put("title", "1"); put("type", "Paint"); put("src", "void mainImage() {}")
                        })
                        put("filter", buildJsonObject {
                            put("title", "1"); put("type", "Filter"); put("src", "// nothing")
                        })
                        put("mover", buildJsonObject {
                            put("title", "1"); put("type", "Mover"); put("src", "// nothing")
                        })
                        put("unknown", buildJsonObject {
                            put("title", "1"); put("type", "Unknown"); put("src", "// nothing")
                        })
                    })
                }
            }

            it("sets prototype from shaderType") {
                expect(show.shaders["proj"]!!.prototype).toBe(ProjectionShader)
                expect(show.shaders["dist"]!!.prototype).toBe(DistortionShader)
                expect(show.shaders["genericPaint"]!!.prototype).toBe(GenericPaintShader)
                expect(show.shaders["toyPaint"]!!.prototype).toBe(ShaderToyPaintShader)
                expect(show.shaders["filter"]!!.prototype).toBe(FilterShader)
                expect(show.shaders["mover"]!!.prototype).toBe(MoverShader)
                expect(show.shaders["unknown"]!!.prototype).toBe(null)
            }

            it("sets resultContentType from shaderType") {
                expect(show.shaders["proj"]!!.resultContentType).toBe(ContentType.UvCoordinateStream)
                expect(show.shaders["dist"]!!.resultContentType).toBe(ContentType.UvCoordinateStream)
                expect(show.shaders["genericPaint"]!!.resultContentType).toBe(ContentType.ColorStream)
                expect(show.shaders["toyPaint"]!!.resultContentType).toBe(ContentType.ColorStream)
                expect(show.shaders["filter"]!!.resultContentType).toBe(ContentType.ColorStream)
                expect(show.shaders["mover"]!!.resultContentType).toBe(ContentType.PanAndTilt)
                expect(show.shaders["unknown"]!!.resultContentType).toBe(ContentType.Unknown)
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