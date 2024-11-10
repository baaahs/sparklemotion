package baaahs.gl.shader

import baaahs.describe
import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslExpr
import baaahs.gl.openShader
import baaahs.gl.override
import baaahs.gl.testToolchain
import baaahs.kotest.value
import baaahs.show.Shader
import baaahs.toBeSpecified
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*

@Suppress("unused")
class OpenShaderSpec : DescribeSpec({
    describe<OpenShaderSpec> {
        val src by value { toBeSpecified<String>() }
        val openShader by value { testToolchain.openShader(Shader("Title", src)) }
        val invoker by value {
            openShader.invoker(
                GlslCode.Namespace("p"), mapOf("time" to GlslExpr("timeVal"), "greenness" to GlslExpr("greennessVal"))
            )
        }
        val abstractFnPortSrc by value {
            "/** @return color */ vec4 imageColor(/** @type uv-coordinate */ uv, /** @type time */ float time);"
        }
        val abstractFnPort by value {
            testToolchain.openShader(Shader("abstract fn port", abstractFnPortSrc)).findInputPort("imageColor")
        }

        beforeEach {
            if (openShader.errors.isNotEmpty()) {
                error("Analysis errors: ${openShader.errors}")
            }
        }

        context("with gl_FragColor output port and gl_FragCoord input port") {
            override(src) {
                """
                    uniform float time; // @type time
                    void main(float greenness) {
                        gl_FragColor = vec4(gl_FragCoord.x, greenness, mod(time, 1.), 1.);
                    };
                """.trimIndent()
            }

            it("generates an invocation statement") {
                invoker.toGlsl("toResultVar")
                    .shouldBe("p_main(greennessVal)")
            }

            context("when invoked from an abstract function") {
                it("generates valid GLSL to invoke the shader") {
//                    abstractFnPort.invoker()
                }
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
                invoker.toGlsl("toResultVar")
                    .shouldBe("toResultVar = p_main(greennessVal)")
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
                invoker.toGlsl("toResultVar")
                    .shouldBe("p_main(greennessVal, toResultVar)")
            }
        }
    }
})
