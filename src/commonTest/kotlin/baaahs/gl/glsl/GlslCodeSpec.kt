package baaahs.gl.glsl

import baaahs.gl.expectValue
import baaahs.gl.override
import baaahs.gl.testPlugins
import baaahs.gl.undefined
import baaahs.only
import baaahs.plugin.PluginRef
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object GlslCodeSpec : Spek({
    describe("statements") {
        val text by value { undefined<String>() }
        val comments by value { emptyList<String>() }
        val statement by value {
            GlslAnalyzer(testPlugins()).findStatements(
                comments.joinToString("\n") { "// $it" } + "\n" + text
            ).only("statement")
        }

        context("variables") {
            val variable by value { (statement as GlslCode.GlslVar).copy(lineNumber = null) }

            context("unqualified") {
                override(text) { "int i;" }
                expectValue(GlslCode.GlslVar(GlslType.Int, "i", "int i;")) { variable }
            }

            context("const") {
                override(text) { "const int i = 3;" }
                expectValue(GlslCode.GlslVar(GlslType.Int, "i", "const int i = 3;", isConst = true)) { variable }
            }

            context("uniform") {
                override(text) { "uniform vec3 vector;" }
                expectValue(GlslCode.GlslVar(GlslType.Vec3, "vector", "uniform vec3 vector;", isUniform = true)) { variable }
            }

            // For now, `varying` on a global var indicates that it's a streamed content type. Maybe worth reconsidering.
            context("varying") {
                override(text) { "varying vec4 otherColor;" }
                expectValue(GlslCode.GlslVar(GlslType.Vec4, "otherColor", "varying vec4 otherColor;", isVarying = true)) { variable }
            }

            context("hints") {
                override(text) { "varying vec4 otherColor; // @type color-stream @something else" }
                override(comments) { listOf(" @type color-stream", " @something else") }

                it("makes hint tags available") {
                    expect(variable.hint?.tags?.get("type")).toBe("color-stream")
                    expect(variable.hint?.tags?.get("something")).toBe("else")
                }
            }
        }

        context("functions") {
            val function by value { (statement as GlslCode.GlslFunction).copy(lineNumber = null) }

            context("simple") {
                override(text) { "float rand(vec2 uv) { return fract(sin(dot(uv.xy,vec2(12.9898,78.233))) * 43758.5453); }" }
                expectValue(
                    GlslCode.GlslFunction(
                        GlslType.Float, "rand",
                        listOf(GlslCode.GlslParam("uv", GlslType.Vec2, isIn = true, lineNumber = 2)),
                        "float rand(vec2 uv) { return fract(sin(dot(uv.xy,vec2(12.9898,78.233))) * 43758.5453); }"
                    )
                ) { function }
            }

            context("void params") {
                override(text) { "float zero( void ) { return 0.; }" }
                expectValue(
                    GlslCode.GlslFunction(
                        GlslType.Float, "zero", emptyList(),
                        "float zero( void ) { return 0.; }"
                    )
                ) { function }
            }

            context("with hints") {
                override(text) { "float zero( void ) { return 0.; }" }
                expectValue(
                    GlslCode.GlslFunction(
                        GlslType.Float, "zero", emptyList(),
                        "float zero( void ) { return 0.; }"
                    )
                ) { function }
            }
        }

        context("struct") {
            val struct by value { (statement as GlslCode.GlslStruct).copy(lineNumber = null) }

            override(text) {
                """
                    struct MovingHead {
                        float pan; // in radians
                        float tilt; // in radians
                    };
                """.trimIndent()
            }

            it("should return a GlslStruct") {
                expect(struct).toBe(GlslCode.GlslStruct(
                                        "MovingHead",
                                        mapOf("pan" to GlslType.Float, "tilt" to GlslType.Float),
                                        null,
                                        false,
                                        text
                                    ))
            }

            context("also declaring a variable") {
                override(text) {
                    """
                        struct MovingHead {
                            float pan; // in radians
                            float tilt; // in radians
                        } movingHead;
                    """.trimIndent()
                }

                it("should return a GlslStruct") {
                    expect(struct).toBe(GlslCode.GlslStruct(
                                                "MovingHead",
                                                mapOf("pan" to GlslType.Float, "tilt" to GlslType.Float),
                                                "movingHead",
                                                false,
                                                text
                                            ))
                }
            }
        }
    }

    describe("GlslVar") {
        context("with comments") {
            val hintClassStr by value { "whatever.package.Plugin:Thing" }
            val glslVar by value {
                GlslCode.GlslVar(
                    GlslType.Float, "varName", isUniform = true,
                    comments = listOf(" @@$hintClassStr", "  key=value", "  key2=value2")
                )
            }

            it("parses hints") {
                expect(glslVar.hint!!.pluginRef).toBe(PluginRef("whatever.package.Plugin", "Thing"))
                expect(glslVar.hint!!.config).toBe(buildJsonObject {
                                    put("key", "value")
                                    put("key2", "value2")
                                })
            }

            context("when package is unspecified") {
                override(hintClassStr) { "Thing" }

                it("defaults to baaahs.Core") {
                    expect(glslVar.hint!!.pluginRef).toBe(PluginRef("baaahs.Core", "Thing"))
                }
            }

            context("when package is partially specified") {
                override(hintClassStr) { "FooPlugin:Thing" }

                it("defaults to baaahs.Core") {
                    expect(glslVar.hint!!.pluginRef).toBe(PluginRef("baaahs.FooPlugin", "Thing"))
                }
            }
        }

        it("englishizes camel case names") {
            expect(GlslCode.GlslVar(GlslType.Vec3, "aManAPlanAAARGHPanamaISay").displayName())
                .toBe("A Man A Plan AAARGH Panama I Say")
        }
    }
})
