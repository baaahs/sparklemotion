package baaahs.gl.glsl

import baaahs.gl.expectValue
import baaahs.gl.override
import baaahs.gl.undefined
import baaahs.only
import baaahs.plugin.PluginRef
import baaahs.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object GlslCodeSpec : Spek({
    describe("statements") {
        val text by value { undefined<String>() }
        val statements by value { GlslParser().findStatements(text) }
        val statement by value { statements.only("statement") }

        context("variables") {
            val variable by value { (statement as GlslCode.GlslVar).copy(lineNumber = null) }

            context("unqualified") {
                override(text) { "int i;" }
                expectValue(GlslCode.GlslVar("i", GlslType.Int, "int i;")) { variable }
            }

            context("with underscores") {
                override(text) { "int _i_i;" }
                expectValue(GlslCode.GlslVar("_i_i", GlslType.Int, "int _i_i;")) { variable }
            }

            context("arrays") {
                override(text) { "vec4 colorsArray[9];" }
                expectValue(GlslCode.GlslVar("colorsArray", GlslType.Vec4.arrayOf(9), "vec4 colorsArray[9];")) { variable }
            }

            context("const") {
                override(text) { "const int i = 3;" }
                expectValue(
                    GlslCode.GlslVar("i", GlslType.Int, "const int i = 3;", isConst = true, initExpr = " = 3")
                ) { variable }
            }

            context("uniform") {
                override(text) { "uniform vec3 vector;" }
                expectValue(
                    GlslCode.GlslVar(
                        "vector",
                        GlslType.Vec3,
                        "uniform vec3 vector;",
                        isUniform = true
                    )
                ) { variable }
            }

            // For now, `varying` on a global var indicates that it's a streamed content type. Maybe worth reconsidering.
            context("varying") {
                override(text) { "varying vec4 otherColor;" }
                expectValue(
                    GlslCode.GlslVar(
                        "otherColor",
                        GlslType.Vec4,
                        "varying vec4 otherColor;",
                        isVarying = true
                    )
                ) { variable }
            }

            context("tag hints") {
                override(text) { "varying vec4 otherColor; // @type color @something else" }

                it("makes hint tags available") {
                    expect(variable.hint?.tag("type")).toBe("color")
                    expect(variable.hint?.tag("something")).toBe("else")
                }

                it("returns null for non-existent tags") {
                    expect(variable.hint?.tag("nope")).toBe(null)
                    expect(variable.hint?.tags("nope")).toBe(emptyList())
                }

                context("when tags are repeated") {
                    override(text) { "// @thing abc\n// @thing def\nvec4 otherColor;" }

                    it("each is available via tags()") {
                        expect(variable.hint!!.tags("thing"))
                            .containsExactly("abc", "def")
                    }

                    it("tag() returns the first") {
                        expect(variable.hint!!.tag("thing"))
                            .toEqual("abc")
                    }
                }

                context("with a plugin reference") {
                    override(text) { "varying vec4 otherColor; // @@ColorPicker default=orange palette=pastels" }

                    it("builds a PluginRef") {
                        expect(variable.hint?.pluginRef)
                            .toEqual(PluginRef("baaahs.Core", "ColorPicker"))
                    }

                    context("when plugin id is fully specified") {
                        override(text) { "varying vec4 otherColor; // @@foo.Plugin:ColorPicker default=orange palette=pastels" }

                        it("builds a PluginRef") {
                            expect(variable.hint?.pluginRef)
                                .toEqual(PluginRef("foo.Plugin", "ColorPicker"))
                        }
                    }

                    context("when plugin id is partially specified") {
                        override(text) { "varying vec4 otherColor; // @@Plugin:ColorPicker default=orange palette=pastels" }

                        it("builds a PluginRef") {
                            expect(variable.hint?.pluginRef)
                                .toEqual(PluginRef("baaahs.Plugin", "ColorPicker"))
                        }
                    }
                }

                context("with leading generic textual comment") {
                    override(text) { "varying vec4 otherColor; // HSB @@ColorPicker default=orange" }

                    it("builds a PluginRef") {
                        expect(variable.hint?.pluginRef)
                            .toEqual(PluginRef("baaahs.Core", "ColorPicker"))
                    }
                }
            }
        }

        context("functions") {
            val function by value { (statement as GlslCode.GlslFunction).copy(lineNumber = null) }

            context("simple") {
                override(text) { "float rand(vec2 uv) { return fract(sin(dot(uv.xy,vec2(12.9898,78.233))) * 43758.5453); }" }
                expectValue(
                    GlslCode.GlslFunction(
                        "rand", GlslType.Float,
                        listOf(GlslCode.GlslParam("uv", GlslType.Vec2, isIn = true, lineNumber = 1)),
                        "float rand(vec2 uv) { return fract(sin(dot(uv.xy,vec2(12.9898,78.233))) * 43758.5453); }"
                    )
                ) { function }
            }

            context("with param names containing underscores") {
                override(text) { "float rand(vec2 _u_v) { return fract(sin(dot(_u_v.xy,vec2(12.9898,78.233))) * 43758.5453); }" }
                expectValue(
                    GlslCode.GlslFunction(
                        "rand", GlslType.Float,
                        listOf(GlslCode.GlslParam("_u_v", GlslType.Vec2, isIn = true, lineNumber = 1)),
                        "float rand(vec2 _u_v) { return fract(sin(dot(_u_v.xy,vec2(12.9898,78.233))) * 43758.5453); }"
                    )
                ) { function }
            }

            context("void params") {
                override(text) { "float zero( void ) { return 0.; }" }
                expectValue(
                    GlslCode.GlslFunction(
                        "zero", GlslType.Float, emptyList(),
                        "float zero( void ) { return 0.; }"
                    )
                ) { function }
            }

            context("with hints") {
                override(text) { "float zero( void ) { return 0.; }" }
                expectValue(
                    GlslCode.GlslFunction(
                        "zero", GlslType.Float, emptyList(),
                        "float zero( void ) { return 0.; }"
                    )
                ) { function }
            }

            context("using a struct in its signature") {
                override(function) { (statements[1] as GlslCode.GlslFunction).copy(lineNumber = null) }
                val expectedStruct by value {
                    GlslCode.GlslStruct(
                        "SomeStruct", mapOf("a" to GlslType.Int),
                        fullText = "struct SomeStruct { int a };", varName = null,
                        lineNumber = 2
                    )
                }

                context("as the return value") {
                    override(text) { "struct SomeStruct { int a };\nSomeStruct rand(vec2 uv) { return xxx; }" }

                    it("has the struct return type") {
                        expect(function).toBe(
                            GlslCode.GlslFunction(
                                "rand", GlslType.Struct(expectedStruct),
                                listOf(GlslCode.GlslParam("uv", GlslType.Vec2, isIn = true, lineNumber = 2)),
                                "SomeStruct rand(vec2 uv) { return xxx; }"
                            )
                        )
                    }
                }

                context("as a param") {
                    override(text) { "struct SomeStruct { int a };\nint rand(SomeStruct someStruct) { return xxx; }" }

                    it("has the struct return type") {
                        expect(function).toBe(
                            GlslCode.GlslFunction(
                                "rand", GlslType.Int,
                                listOf(GlslCode.GlslParam("someStruct", GlslType.Struct(expectedStruct), isIn = true, lineNumber = 2)),
                                "int rand(SomeStruct someStruct) { return xxx; }"
                            )
                        )
                    }
                }
            }
        }

        context("struct") {
            val struct by value { (statements[0] as GlslCode.GlslStruct).copy(lineNumber = null) }
            val expectedStruct by value {
                GlslCode.GlslStruct(
                    "MovingHead",
                    mapOf("pan" to GlslType.Float, "tilt" to GlslType.Float),
                    null,
                    false,
                    """
                        struct MovingHead {
                            float pan; // in radians
                            float tilt; // in radians
                        };
                    """.trimIndent()
                )
            }

            override(text) {
                """
                    struct MovingHead {
                        float pan; // in radians
                        float tilt; // in radians
                    };
                    
                    uniform MovingHead movingHead;
                """.trimIndent()
            }

            it("should return a GlslStruct") {
                expect(struct).toBe(expectedStruct)
                expect(struct.varName).toBe(null)
            }

            it("should apply the same struct type to uniforms") {
                expect((statements[1] as GlslCode.GlslVar))
                    .toBe(
                        GlslCode.GlslVar(
                            "movingHead",
                            GlslType.Struct(expectedStruct.copy(lineNumber = 2)),
                            "uniform MovingHead movingHead;",
                            isUniform = true,
                            lineNumber = 6
                        )
                    )
            }

            it("should match structs with the same fields") {
                expect(struct.glslType.matches(
                    GlslType.Struct(
                        "MovingHead",
                        GlslType.Field("pan", GlslType.Float),
                        GlslType.Field("tilt", GlslType.Float),
                    )
                )).toBe(true)
            }

            it("should match structs with the same fields out of order") {
                expect(struct.glslType.matches(
                    GlslType.Struct(
                        "MovingHead",
                        GlslType.Field("tilt", GlslType.Float),
                        GlslType.Field("pan", GlslType.Float),
                    )
                )).toBe(true)
            }

            it("should not match structs with the same fields but different types") {
                expect(struct.glslType.matches(
                    GlslType.Struct(
                        "MovingHead",
                        GlslType.Field("pan", GlslType.Float),
                        GlslType.Field("tilt", GlslType.Vec2),
                    )
                )).toBe(false)
            }

            it("should not match structs with a different name") {
                expect(struct.glslType.matches(
                    GlslType.Struct(
                        "MovingHeadz",
                        GlslType.Field("pan", GlslType.Float),
                        GlslType.Field("tilt", GlslType.Float),
                    )
                )).toBe(false)
            }

            it("should not match structs with a subset of fields") {
                expect(struct.glslType.matches(
                    GlslType.Struct(
                        "MovingHead",
                        GlslType.Field("pan", GlslType.Float),
                    )
                )).toBe(false)
            }

            it("should match structs with a superset of fields") {
                expect(struct.glslType.matches(
                    GlslType.Struct(
                        "MovingHead",
                        GlslType.Field("pan", GlslType.Float),
                        GlslType.Field("tilt", GlslType.Float),
                        GlslType.Field("dimmer", GlslType.Float),
                    )
                )).toBe(true)
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
                    expect(struct).toBe(
                        GlslCode.GlslStruct(
                            "MovingHead",
                            mapOf("pan" to GlslType.Float, "tilt" to GlslType.Float),
                            "movingHead",
                            false,
                            text
                        )
                    )
                }
            }
        }
    }

    describe("GlslVar") {
        context("with comments") {
            val hintClassStr by value { "whatever.package.Plugin:Thing" }
            val glslVar by value {
                GlslCode.GlslVar(
                    "varName", GlslType.Float, isUniform = true,
                    comments = listOf(" @@$hintClassStr", "  key=value", "  key2=value2")
                )
            }

            it("parses hints") {
                expect(glslVar.hint!!.pluginRef)
                    .toBe(PluginRef("whatever.package.Plugin", "Thing"))

                expect(glslVar.hint!!.config)
                    .toBe(buildJsonObject {
                        put("key", "value")
                        put("key2", "value2")
                    })
            }

            context("when package is unspecified") {
                override(hintClassStr) { "Thing" }

                it("defaults to baaahs.Core") {
                    expect(glslVar.hint!!.pluginRef)
                        .toBe(PluginRef("baaahs.Core", "Thing"))
                }
            }

            context("when package is partially specified") {
                override(hintClassStr) { "FooPlugin:Thing" }

                it("defaults to baaahs.Core") {
                    expect(glslVar.hint!!.pluginRef)
                        .toBe(PluginRef("baaahs.FooPlugin", "Thing"))
                }
            }

            context("when there's text before the plugin ref") {
                override(glslVar) {
                    GlslCode.GlslVar(
                        "varName", GlslType.Float, isUniform = true,
                        comments = listOf(" comment here @@$hintClassStr", "  key=value", "  key2=value2")
                    )
                }

                it("parses the PluginRef") {
                    expect(glslVar.hint!!.pluginRef)
                        .toBe(PluginRef("whatever.package.Plugin", "Thing"))

                    expect(glslVar.hint!!.config)
                        .toBe(buildJsonObject {
                            put("key", "value")
                            put("key2", "value2")
                        })
                }
            }
        }

        it("englishizes camel case names") {
            expect(GlslCode.GlslVar("aManAPlanAAARGHPanamaISay", GlslType.Vec3).title)
                .toBe("A Man A Plan AAARGH Panama I Say")
        }
    }
})
