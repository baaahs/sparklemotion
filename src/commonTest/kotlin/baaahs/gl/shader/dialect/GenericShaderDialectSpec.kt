package baaahs.gl.shader.dialect

import baaahs.describe
import baaahs.gl.glsl.GlslError
import baaahs.gl.glsl.GlslType
import baaahs.gl.override
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.gl.shader.OutputPort
import baaahs.gl.testToolchain
import baaahs.toBeSpecified
import baaahs.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.contains
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek

@Suppress("unused")
object GenericShaderDialectSpec : Spek({
    describe<GenericShaderDialect> {
        val src by value<String> { toBeSpecified() }
        val dialect by value { GenericShaderDialect }
        val glslCode by value { testToolchain.parse(src) }
        val analyzer by value { dialect.match(glslCode, testToolchain.plugins) }
        val matchLevel by value { analyzer.matchLevel }
        val shaderAnalysis by value { dialect.analyze(testToolchain.parse(src), testToolchain.plugins) }
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
                expect(matchLevel).toEqual(MatchLevel.Poor)
            }

            it("finds the input port") {
                expect(shaderAnalysis.inputPorts.str()).containsExactly(
                    "time time:float (Time)"
                )
            }

            context("when a shader refers to gl_FragColor") {
                override(src) { "void main() { gl_FragColor = vec4(0.); }" }

                it("finds the output port") {
                    expect(shaderAnalysis.outputPorts).containsExactly(
                        OutputPort(ContentType.Color, description = "Output Color", id = "gl_FragColor")
                    )
                }
            }

            context("when a shader refers to gl_FragCoord") {
                override(src) { "vec4 main() { return vec4(gl_FragCoord.xy, 0., 1.); }" }

                it("includes it as an input port") {
                    expect(openShader.inputPorts).containsExactly(
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
                    expect(matchLevel).toEqual(MatchLevel.Poor)
                }

                it("finds the input port") {
                    expect(openShader.inputPorts.str()).containsExactly(
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
                    expect(shaderAnalysis.isValid).toBe(false)

                    expect(shaderAnalysis.errors).contains(
                        GlslError("Too many output ports found: [fragColor, gl_FragColor, other].", row = 1)
                    )
                }
            }
        }

        context("a shader without a main() function") {
            override(src) { "void mainImage(void) { ... };" }
            it("is not a match") {
                expect(matchLevel).toEqual(MatchLevel.NoMatch)
            }
        }
    }
})
