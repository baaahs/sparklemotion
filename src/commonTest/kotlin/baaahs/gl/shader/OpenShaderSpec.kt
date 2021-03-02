package baaahs.gl.shader

import baaahs.describe
import baaahs.gl.glsl.GlslCode
import baaahs.gl.override
import baaahs.gl.testToolchain
import baaahs.show.Shader
import baaahs.toEqual
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek

@Suppress("unused")
object OpenShaderSpec : Spek({
    describe<OpenShaderSpec> {
        val src by value {
            "void main(float intensity) { gl_FragColor = vec4(gl_FragCoord, 0., 1.); };"
        }
        val shader by value { Shader("Title", src) }
        val shaderAnalysis by value { testToolchain.analyze(shader) }
        val openShader by value { testToolchain.openShader(shaderAnalysis) }
        val invocationStatement by value {
            openShader.invoker(
                GlslCode.Namespace("p"), mapOf("time" to "timeVal", "intensity" to "intensityVal")
            ).toGlsl("toResultVar")
        }

        it("generates an invocation statement") {
            expect(invocationStatement).toEqual("p_main(intensityVal)")
        }

        context("with additional parameters") {
            override(src) {
                """
                    // @return time
                    float main(
                        float intensity,
                        float time // @type time
                    ) { return time + sin(time) * intensity; }
                """.trimIndent()
            }

            it("generates an invocation statement") {
                expect(invocationStatement).toEqual("toResultVar = p_main(intensityVal, timeVal)")
            }
        }
    }
})
