package baaahs.gl.shader

import baaahs.describe
import baaahs.gl.glsl.GlslAnalyzer
import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslError
import baaahs.gl.override
import baaahs.gl.patch.ContentType.Companion.Color
import baaahs.gl.testPlugins
import baaahs.show.ShaderOutPortRef
import baaahs.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.*
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek

@Suppress("unused")
object ShaderToyPaintShaderSpec : Spek({
    describe<ShaderToyPaintShaderSpec> {
        val src by value { "void mainImage(out vec4 fragColor, in vec2 fragCoord) { ... };" }
        val prototype by value { ShaderToyPaintShader }
        val openShader by value { GlslAnalyzer(testPlugins()).openShader(src) }
        val glslCode by value { openShader.glslCode }
        val invocationStatement by value {
            openShader.invocationGlsl(
                GlslCode.Namespace("p"), "toResultVar",
                mapOf("fragCoord" to "fragCoordVal.xy", "intensity" to "intensityVal")
            )
        }

        context("shaders having a void mainImage(out vec4, in vec2) function") {
            it("is an good match") {
                expect(prototype.matches(glslCode)).toEqual(MatchLevel.Good)
            }

            it("finds the input port") {
                expect(openShader.inputPorts.str()).containsExactly(
                    "fragCoord uv-coordinate/vec2"
                )
            }

            it("finds the output port") {
                expect(openShader.outputPort).toEqual(
                    OutputPort(Color, description = "Output Color", id = ShaderOutPortRef.ReturnValue)
                )
            }

            it("generates an invocation statement") {
                expect(invocationStatement).toEqual("p_mainImage(toResultVar, fragCoordVal.xy)")
            }

            context("with the parameters out of order") {
                override(src) { "void mainImage(in vec2 fragCoord, out vec4 fragColor) { ... };" }

                it("is an good match") {
                    expect(prototype.matches(glslCode)).toEqual(MatchLevel.Good)
                }

                it("generates an invocation statement") {
                    expect(invocationStatement).toEqual("p_mainImage(fragCoordVal.xy, toResultVar)")
                }
            }

            context("with additional parameters") {
                override(src) { "void mainImage(in vec2 fragCoord, out vec4 fragColor, in float intensity) { ... };" }

                it("is an good match") {
                    expect(prototype.matches(glslCode)).toEqual(MatchLevel.Good)
                }

                it("finds the input port") {
                    expect(openShader.inputPorts.str()).containsExactly(
                        "fragCoord uv-coordinate/vec2",
                        "intensity ???/float"
                    )
                }

                it("generates an invocation statement") {
                    expect(invocationStatement).toEqual("p_mainImage(fragCoordVal.xy, toResultVar, intensityVal)")
                }
            }

            context("with uniforms") {
                override(src) {
                    "uniform float intensity;\nvoid mainImage(in vec2 fragCoord, out vec4 fragColor) { ... };"
                }

                it("finds the input port") {
                    expect(openShader.inputPorts.str()).contains.inAnyOrder.only.values(
                        "fragCoord uv-coordinate/vec2",
                        "intensity ???/float"
                    )
                }
            }

            context("with multiple out parameters") {
                override(src) {
                    "uniform float intensity;\nvoid mainImage(in vec2 fragCoord, out vec4 fragColor, out float other) { ... };"
                }

                it("fails to validate") {
                    expect(prototype.validate(glslCode)).containsExactly(
                        GlslError("Multiple out parameters aren't allowed on mainImage().", row = 2)
                    )
                }
            }

            context("with missing parameters") {
                override(src) { "void mainImage() { ... };" }
                it("is still a match") {
                    expect(prototype.matches(glslCode)).toEqual(MatchLevel.Good)
                }

                it("fails validation") {
                    expect(prototype.validate(glslCode)).containsExactly(
                        GlslError(
                            "Missing arguments. " +
                                    "Signature should be \"void mainImage(in vec2 fragCoord, out vec4 fragColor)\".",
                            row = 1
                        )
                    )
                }
            }
        }

        context("shaders without a mainImage function") {
            override(src) { "void main(void) { ... };" }

            it("is not a match") {
                expect(prototype.matches(glslCode)).toEqual(MatchLevel.NoMatch)
            }

            it("fails to validate") {
                expect(prototype.validate(glslCode)).containsExactly(
                    GlslError("No entry point function \"mainImage()\" among [main]")
                )
            }
        }
    }
})

fun List<InputPort>.str(): List<String> {
    return map {
        with(it) { "$id ${contentType?.id ?: "???"}/${type.glslLiteral}" }
    }
}