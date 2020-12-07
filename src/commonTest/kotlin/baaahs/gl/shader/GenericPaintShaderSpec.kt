package baaahs.gl.shader

import baaahs.describe
import baaahs.gl.glsl.GlslAnalyzer
import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslError
import baaahs.gl.override
import baaahs.gl.patch.ContentType
import baaahs.gl.testPlugins
import baaahs.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek

@Suppress("unused")
object GenericPaintShaderSpec : Spek({
    describe<GenericPaintShaderSpec> {
        val src by value { "void main(void) { gl_FragColor = vec4(gl_FragCoord, 0., 1.); };" }
        val prototype by value { GenericPaintShader }
        val openShader by value { GlslAnalyzer(testPlugins()).openShader(src) }
        val glslCode by value { openShader.glslCode }
        val invocationStatement by value {
            openShader.invocationGlsl(
                GlslCode.Namespace("p"), "toResultVar",
                mapOf("fragCoord" to "fragCoordVal", "intensity" to "intensityVal")
            )
        }

        context("shaders having a main() function") {
            it("is a good match") {
                expect(prototype.matches(glslCode)).toEqual(MatchLevel.Good)
            }

            it("finds the input port") {
                expect(openShader.inputPorts.str()).containsExactly(
                    "gl_FragCoord uv-coordinate-stream/vec4"
                )
            }

            it("finds the output port") {
                expect(openShader.outputPort).toEqual(
                    OutputPort(ContentType.ColorStream, description = "Output Color", id = "gl_FragColor"))
            }

            it("generates an invocation statement") {
                expect(invocationStatement).toEqual("p_main()")
            }

            context("if it doesn't refer to gl_FragColor") {
                override(src) { "void main(float intensity) { };" }

                it("fails to validate") {
                    expect(prototype.validate(glslCode)).containsExactly(
                        GlslError("Shader doesn't write to gl_FragColor.", 1)
                    )
                }
            }

            context("with additional parameters") {
                override(src) { "void main(float intensity) { gl_FragColor = vec4(gl_FragCoord, 0., 1.); };" }

                it("is an good match") {
                    expect(prototype.matches(glslCode)).toEqual(MatchLevel.Good)
                }

                it("finds the input port") {
                    expect(openShader.inputPorts.str()).containsExactly(
                        "gl_FragCoord uv-coordinate-stream/vec4",
                        "intensity ???/float"
                    )
                }

                it("generates an invocation statement") {
                    expect(invocationStatement).toEqual("p_main(intensityVal)")
                }
            }

            context("with multiple out parameters") {
                override(src) {
                    "void main(in vec2 fragCoord, out vec4 fragColor, out float other) { gl_FragColor = vec4(gl_FragCoord, 0., 1.); };"
                }

                it("fails to validate") {
                    expect(prototype.validate(glslCode)).containsExactly(
                        GlslError("Multiple out parameters aren't allowed on main().", row = 1)
                    )
                }
            }
        }

        context("shaders without a main() function") {
            override(src) { "void mainImage(void) { ... };" }
            it("is not a match") {
                expect(prototype.matches(glslCode)).toEqual(MatchLevel.NoMatch)
            }
        }
    }
})
