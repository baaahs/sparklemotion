package baaahs.gl.glsl

import baaahs.gl.expectValue
import baaahs.gl.override
import baaahs.gl.undefined
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.expect

object GlslCodeSpec : Spek({
    describe("statements") {
        val text by value { undefined<String>() }
        val comments by value { emptyList<String>() }
        val statement by value { GlslAnalyzer.GlslStatement(text, comments) }

        context("variables") {
            val variable by value { statement.asVarOrNull() }

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
                    expect("color-stream") { variable?.hint?.tags?.get("type") }
                    expect("else") { variable?.hint?.tags?.get("something") }
                }
            }
        }

        context("functions") {
            val function by value { statement.asFunctionOrNull() }

            context("simple") {
                override(text) { "float rand(vec2 uv) { return fract(sin(dot(uv.xy,vec2(12.9898,78.233))) * 43758.5453); }" }
                expectValue(
                    GlslCode.GlslFunction(
                        GlslType.Float, "rand", "vec2 uv",
                        "float rand(vec2 uv) { return fract(sin(dot(uv.xy,vec2(12.9898,78.233))) * 43758.5453); }"
                    )
                ) { function }
            }
        }

        context("struct") {
            val struct by value { statement.asStructOrNull() }

            override(text) {
                """
                        struct MovingHead {
                            float pan; // in radians
                            float tilt; // in radians
                        };
                    """.trimIndent()
            }

            it("should return a GlslStruct") {
                expect(
                    GlslCode.GlslStruct(
                        "MovingHead",
                        mapOf("pan" to GlslType.Float, "tilt" to GlslType.Float),
                        null,
                        false,
                        text
                    )
                ) { struct }
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
                    expect(
                        GlslCode.GlslStruct(
                            "MovingHead",
                            mapOf("pan" to GlslType.Float, "tilt" to GlslType.Float),
                            "movingHead",
                            false,
                            text
                        )
                    ) { struct }
                }
            }
        }
    }
})
