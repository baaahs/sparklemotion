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
object GenericShaderPrototypeSpec : Spek({
    describe<GenericShaderPrototypeSpec> {
        val src by value {
            """
                // @type time
                float main(
                    float time // @type time
                ) { return time + sin(time); }
            """.trimIndent()
        }
        val prototype by value { GenericShaderPrototype }
        val openShader by value { GlslAnalyzer(testPlugins()).openShader(src) }
        val glslCode by value { openShader.glslCode }
        val invocationStatement by value {
            openShader.invocationGlsl(
                GlslCode.Namespace("p"), "toResultVar",
                mapOf("time" to "timeVal", "intensity" to "intensityVal")
            )
        }

        context("shaders having a main() function") {
            it("is a poor match (so this one acts as a fallback)") {
                expect(prototype.matches(glslCode)).toEqual(MatchLevel.Poor)
            }

            it("finds the input port") {
                expect(openShader.inputPorts.str()).containsExactly(
                    "time time/float"
                )
            }

            it("finds the output port") {
                expect(openShader.outputPort).toEqual(
                    OutputPort(ContentType.ColorStream, description = "Output Color", id = "gl_FragColor"))
            }

            it("generates an invocation statement") {
                expect(invocationStatement).toEqual("toResultVar = p_main(timeVal)")
            }

            context("with additional parameters") {
                override(src) { "void main(float intensity) { gl_FragColor = vec4(gl_FragCoord, 0., 1.); };" }

                it("continues to be a match") {
                    expect(prototype.matches(glslCode)).toEqual(MatchLevel.Poor)
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
