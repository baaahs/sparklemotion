package baaahs.gl.shader.dialect

import baaahs.describe
import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslError
import baaahs.gl.glsl.GlslExpr
import baaahs.gl.override
import baaahs.gl.patch.ContentType.Companion.Color
import baaahs.gl.shader.InputPort
import baaahs.gl.shader.OpenShader
import baaahs.gl.shader.OutputPort
import baaahs.gl.shader.type.PaintShader
import baaahs.gl.testToolchain
import baaahs.kotest.value
import baaahs.show.Shader
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder

@Suppress("unused")
object ShaderToyShaderDialectSpec : DescribeSpec({
    describe<ShaderToyShaderDialectSpec> {
        val src by value { "void mainImage(out vec4 fragColor, in vec2 fragCoord) { ... };" }
        val shader by value { Shader("Title", src) }
        val dialect by value { ShaderToyShaderDialect }
        val glslCode by value { testToolchain.parse(src) }
        val analyzer by value { dialect.match(glslCode, testToolchain.plugins) }
        val matchLevel by value { analyzer.matchLevel }
        val shaderAnalysis by value { analyzer.analyze() }
        val openShader by value { OpenShader.Base(shaderAnalysis, PaintShader) }
        val invocationStatement by value {
            openShader.invoker(
                GlslCode.Namespace("p"), mapOf("fragCoord" to GlslExpr("fragCoordVal.xy"), "intensity" to GlslExpr("intensityVal"))
            ).toGlsl("toResultVar")
        }

        context("shaders having a void mainImage(out vec4, in vec2) function") {
            it("is an good match") {
                matchLevel.shouldBe(MatchLevel.Good)
            }

            it("finds the input port") {
                shaderAnalysis.inputPorts.str().shouldContainExactly(
                    "fragCoord uv-coordinate:vec2 (U/V Coordinates)"
                )
            }

            it("finds the output port") {
                shaderAnalysis.outputPorts.shouldContainExactly(
                    OutputPort(Color, description = "Output Color", id = "fragColor", isParam = true)
                )
            }

            it("generates an invocation statement") {
                invocationStatement.shouldBe("p_mainImage(toResultVar, fragCoordVal.xy)")
            }

            context("with the parameters out of order") {
                override(src) { "void mainImage(in vec2 fragCoord, out vec4 fragColor) { ... };" }

                it("is an good match") {
                    matchLevel.shouldBe(MatchLevel.Good)
                }

                it("generates an invocation statement") {
                    invocationStatement.shouldBe("p_mainImage(fragCoordVal.xy, toResultVar)")
                }
            }

            context("with additional parameters") {
                override(src) { "void mainImage(in vec2 fragCoord, out vec4 fragColor, in float intensity) { ... };" }

                it("is an good match") {
                    matchLevel.shouldBe(MatchLevel.Good)
                }

                it("finds the input port") {
                    shaderAnalysis.inputPorts.str().shouldContainExactly(
                        "fragCoord uv-coordinate:vec2 (U/V Coordinates)",
                        "intensity unknown/float:float (Intensity)"
                    )
                }

                it("generates an invocation statement") {
                    invocationStatement.shouldBe("p_mainImage(fragCoordVal.xy, toResultVar, intensityVal)")
                }
            }

            context("with uniforms") {
                override(src) {
                    "uniform float intensity;\nvoid mainImage(in vec2 fragCoord, out vec4 fragColor) { ... };"
                }

                it("finds the input port") {
                    shaderAnalysis.inputPorts.str().shouldContainExactlyInAnyOrder(
                        "fragCoord uv-coordinate:vec2 (U/V Coordinates)",
                        "intensity unknown/float:float (Intensity)"
                    )
                }
            }

            context("with ShaderToy magic uniforms") {
                override(src) {
                    """
                        void mainImage(in vec2 fragCoord, out vec4 fragColor) { iResolution iTime iMouse };" }
                    """.trimIndent()
                }

                it("identifies the uniforms and maps them to the correct content types") {
                    shaderAnalysis.inputPorts.str().shouldContainExactlyInAnyOrder(
                        "fragCoord uv-coordinate:vec2 (U/V Coordinates)",
                        "iResolution resolution:vec3 (Resolution)",
                        "iTime time:float (Time)",
                        "iMouse mouse:vec4 (Mouse)"
                    )
                }
            }

            context("with multiple out parameters") {
                override(src) {
                    "uniform float intensity;\nvoid mainImage(in vec2 fragCoord, out vec4 fragColor, out float other) { ... };"
                }

                it("fails to validate") {
                    shaderAnalysis.isValid.shouldBeFalse()

                    shaderAnalysis.errors.contains(
                        GlslError("Too many output ports found: [fragColor, other].", row = 2)
                    )
                }
            }

            context("with missing parameters") {
                override(src) { "void mainImage() { ... };" }

                it("is still a match") {
                    matchLevel.shouldBe(MatchLevel.Good)
                }

                it("fails validation") {
                    shaderAnalysis.isValid.shouldBeFalse()
                    shaderAnalysis.errors.shouldContainExactly(
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
                matchLevel.shouldBe(MatchLevel.NoMatch)
            }

            it("fails to validate") {
                shaderAnalysis.isValid.shouldBeFalse()
                shaderAnalysis.errors.shouldContainExactly(
                    GlslError("No entry point \"mainImage\" among [main]"),
                    GlslError("No output port found.")
                )
            }
        }
    }
})

fun List<InputPort>.str(): List<String> {
    return map {
        with(it) { "$id ${contentType.id}:${type.glslLiteral} ($title)" }
    }
}