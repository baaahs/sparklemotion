package baaahs.gl.shader.dialect

import baaahs.describe
import baaahs.gl.glsl.GlslError
import baaahs.gl.glsl.GlslType
import baaahs.gl.override
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.gl.shader.OutputPort
import baaahs.gl.testToolchain
import baaahs.kotest.value
import baaahs.toBeSpecified
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.collections.shouldContainExactly

@Suppress("unused")
object GenericShaderDialectSpec : DescribeSpec({
    describe<GenericShaderDialect> {
        val src by value<String> { toBeSpecified() }
        val dialect by value { GenericShaderDialect }
        val glslCode by value { testToolchain.parse(src) }
        val analyzer by value { dialect.match(glslCode, testToolchain.plugins) }
        val matchLevel by value { analyzer.matchLevel }
        val shaderAnalysis by value { analyzer.analyze() }
        val openShader by value { testToolchain.openShader(shaderAnalysis) }

        context("a shader having a main() function") {
            override(src) {
                """
                    // @return time
                    float main(
                        float time // @type time
                    ) { return time + sin(time); }
                """.trimIndent()
            }

            it("is a poor match (so this one acts as a fallback)") {
                matchLevel.shouldBe(MatchLevel.Poor)
            }

            it("finds the input port") {
                shaderAnalysis.inputPorts.str().shouldContainExactly(
                    "time time:float (Time)"
                )
            }

            context("when a shader refers to gl_FragColor") {
                override(src) { "void main() { gl_FragColor = vec4(0.); }" }

                it("finds the output port") {
                    shaderAnalysis.outputPorts.shouldContainExactly(
                        OutputPort(ContentType.Color, description = "Output Color", id = "gl_FragColor")
                    )
                }
            }

            context("when a shader refers to gl_FragCoord") {
                override(src) { "vec4 main() { return vec4(gl_FragCoord.xy, 0., 1.); }" }

                it("includes it as an input port") {
                    openShader.inputPorts.shouldContainExactly(
                        InputPort(
                            "gl_FragCoord", ContentType.UvCoordinate, GlslType.Vec4,
                            "Coordinates", isImplicit = true
                        )
                    )
                }
            }

            context("with additional parameters") {
                override(src) { "void main(float intensity) { gl_FragColor = vec4(gl_FragCoord, 0., 1.); };" }

                it("continues to be a match") {
                    matchLevel.shouldBe(MatchLevel.Poor)
                }

                it("finds the input port") {
                    openShader.inputPorts.str().shouldContainExactly(
                        "gl_FragCoord uv-coordinate:vec4 (Coordinates)",
                        "intensity unknown/float:float (Intensity)"
                    )
                }
            }

            context("with multiple out parameters") {
                override(src) {
                    "void main(in vec2 fragCoord, out vec4 fragColor, out float other) { gl_FragColor = vec4(gl_FragCoord, 0., 1.); };"
                }

                it("fails to validate") {
                    shaderAnalysis.isValid.shouldBeFalse()

                    shaderAnalysis.errors.contains(
                        GlslError("Too many output ports found: [fragColor, gl_FragColor, other].", row = 1)
                    )
                }
            }
        }

        context("a shader without a main() function") {
            override(src) { "void mainImage(void) { ... };" }
            it("is not a match") {
                matchLevel.shouldBe(MatchLevel.NoMatch)
            }
        }
    }
})
