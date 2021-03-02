package baaahs.gl.shader

import baaahs.describe
import baaahs.gl.glsl.GlslCode
import baaahs.gl.override
import baaahs.gl.testToolchain
import baaahs.show.Shader
import baaahs.toBeSpecified
import baaahs.toEqual
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek

@Suppress("unused")
object OpenShaderSpec : Spek({
    describe<OpenShaderSpec> {
        val src by value { toBeSpecified<String>() }
        val shader by value { Shader("Title", src) }
        val shaderAnalysis by value { testToolchain.analyze(shader) }
        val openShader by value { testToolchain.openShader(shaderAnalysis) }
        val invocationStatement by value {
            openShader.invoker(
                GlslCode.Namespace("p"), mapOf("time" to "timeVal", "greenness" to "greennessVal")
            ).toGlsl("toResultVar")
        }

        beforeEachTest {
            if (openShader.errors.isNotEmpty()) {
                error("Analysis errors: ${openShader.errors}")
            }
        }

        context("with gl_FragColor output port") {
            override(src) {
                """
                    uniform float time; // @type time
                    void main(float greenness) {
                        gl_FragColor = vec4(gl_FragCoord.x, greenness, mod(time, 1.), 1.);
                    };
                """.trimIndent()
            }

            it("generates an invocation statement") {
                expect(invocationStatement).toEqual("p_main(greennessVal)")
            }
        }


        context("with return value output port") {
            override(src) {
                """
                    uniform float time; // @type time
                    // @return color
                    vec4 main(float greenness) {
                        return vec4(gl_FragCoord.x, greenness, mod(time, 1.), 1.);
                    }
                """.trimIndent()
            }

            it("generates an invocation statement") {
                expect(invocationStatement).toEqual("toResultVar = p_main(greennessVal)")
            }
        }

        context("with out arg output port") {
            override(src) {
                """
                    uniform float time; // @type time
                    // @param outColor color
                    void main(float greenness, out vec4 outColor) {
                        outColor = vec4(gl_FragCoord.x, greenness, mod(time, 1.), 1.);
                    }
                """.trimIndent()
            }

            it("generates an invocation statement") {
                expect(invocationStatement).toEqual("p_main(greennessVal, toResultVar)")
            }
        }
    }
})
