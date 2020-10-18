package baaahs.gl.shader

import baaahs.gl.glsl.GlslAnalyzer
import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslType
import baaahs.gl.override
import baaahs.gl.patch.AutoWirer
import baaahs.gl.patch.ContentType
import baaahs.glsl.Shaders
import baaahs.plugin.Plugins
import baaahs.show.ShaderChannel
import baaahs.show.mutable.MutableConstPort
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutableShaderChannel
import baaahs.show.mutable.MutableShaderOutPort
import baaahs.toBeSpecified
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.expect

object FilterShaderSpec : Spek({
    describe("FilterShader") {
        val shaderText by value<String> { toBeSpecified() }
        val shader by value { GlslAnalyzer(Plugins.safe()).openShader(shaderText) as FilterShader }
        val namespace by value { GlslCode.Namespace("p0") }

        context("cross-fade between shaders") {
            override(shaderText) {
                /**language=glsl*/
                """
                    // Fade Filter
                    
                    uniform float fade;
                    varying vec4 otherColorStream; // @type color-stream

                    vec4 mainFilter(vec4 colorIn) {
                        return mix(colorIn, otherColorStream, fade);
                    }
                """.trimIndent()
            }

            it("finds magic uniforms") {
                expect(listOf(
                    InputPort("gl_FragColor", GlslType.Vec4, "Input Color", ContentType.ColorStream),
                    InputPort("fade", GlslType.Float, "Fade"),
                    InputPort("otherColorStream", GlslType.Vec4, "Other Color Stream", ContentType.ColorStream)
                )) { shader.inputPorts.map { it.copy(glslVar = null) } }
            }

            it("generates function declarations") {
                expect(
                    """
                        #line 4
                         
                        vec4 p0_mainFilter(vec4 colorIn) {
                            return mix(colorIn, p0_otherColorStream, p0_fade);
                        }
                    """.trimIndent()
                ) {
                    shader.toGlsl(
                        namespace, mapOf(
                            "resolution" to "in_resolution",
                            "blueness" to "aquamarinity",
                            "identity" to "p0_identity",
                            "gl_FragColor" to "sm_result"
                        )).trim()
                }
            }

            context("in a patch using a shader channel") {
                val autoWirer by value { AutoWirer(Plugins.safe()) }
                val otherChannel by value { ShaderChannel("other") }
                val linkedPatch by value {
                    MutablePatch {
                        val redInstance =
                            addShaderInstance(Shaders.red) {}

                        addShaderInstance(Shaders.blue) {
                            shaderChannel = MutableShaderChannel(otherChannel.id)
                        }

                        addShaderInstance(shader.shader) {
                            link("gl_FragColor", MutableShaderOutPort(redInstance))
                            link("otherColorStream", MutableShaderChannel(otherChannel.id))
                            link("fade", MutableConstPort(".5"))
                        }
                    }.openForPreview(autoWirer)
                }

                it("accepts color streams from multiple shaders") {
                    expect("""
                        #version *

                        #ifdef GL_ES
                        precision mediump float;
                        #endif

                        // SparkleMotion-generated GLSL

                        layout(location = 0) out vec4 sm_result;


                        // Shader: Solid Blue; namespace: p0
                        // Solid Blue

                        vec4 p0_solidBluei_result = vec4(0., 0., 0., 1.);

                        #line 1
                        void p0_solidBlue_mainImage(out vec4 fragColor, in vec2 fragCoord) {
                            fragColor = (0., 0., 1., 1.);
                        }

                        // Shader: Solid Red; namespace: p1
                        // Solid Red

                        vec4 p1_solidRedi_result = vec4(0., 0., 0., 1.);

                        #line 1
                        void p1_solidRed_mainImage(out vec4 fragColor, in vec2 fragCoord) {
                            fragColor = (1., 0., 0., 1.);
                        }

                        // Shader: Fade Filter; namespace: p2
                        // Fade Filter

                        vec4 p2_fadeFilteri_result = vec4(0., 0., 0., 1.);

                        #line 4
                         
                        vec4 p2_fadeFilter_mainFilter(vec4 colorIn) {
                            return mix(colorIn, p0_solidBluei_result, (.5));
                        }


                        #line 10001
                        void main() {
                          p0_solidBlue_mainImage(p0_solidBluei_result, sm_FragCoord.xy); // Solid Blue
                          p1_solidRed_mainImage(p1_solidRedi_result, sm_FragCoord.xy); // Solid Red
                          p2_fadeFilteri_result = p2_fadeFilter_mainFilter(p1_solidRedi_result); // Fade Filter
                          sm_result = p2_fadeFilteri_result;
                        }


                    """.trimIndent()) { linkedPatch!!.toFullGlsl("*") }
                }
            }

            it("generates invocation GLSL") {
                expect("resultVar = p0_mainFilter(boof)") {
                    shader.invocationGlsl(namespace, "resultVar", mapOf("gl_FragColor" to "boof"))
                }
            }
        }
    }
})
