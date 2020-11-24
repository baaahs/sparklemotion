package baaahs.gl.shader

import baaahs.gl.glsl.GlslAnalyzer
import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslType
import baaahs.gl.kexpect
import baaahs.gl.override
import baaahs.gl.patch.AutoWirer
import baaahs.gl.patch.ContentType
import baaahs.gl.testPlugins
import baaahs.glsl.Shaders
import baaahs.show.ShaderChannel
import baaahs.show.mutable.MutableConstPort
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutableShaderChannel
import baaahs.show.mutable.MutableShaderOutPort
import baaahs.toBeSpecified
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object FilterShaderSpec : Spek({
    describe("FilterShader") {
        val shaderText by value<String> { toBeSpecified() }
        val shader by value { GlslAnalyzer(testPlugins()).openShader(shaderText) as FilterShader }
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
                expect(shader.inputPorts.map { it.copy(glslArgSite = null) })
                    .containsExactly(
                        InputPort("gl_FragColor", GlslType.Vec4, "Input Color", ContentType.ColorStream),
                        InputPort("fade", GlslType.Float, "Fade"),
                        InputPort("otherColorStream", GlslType.Vec4, "Other Color Stream", ContentType.ColorStream)
                    )
            }

            it("generates function declarations") {
                expect(shader.toGlsl(
                    namespace, mapOf(
                        "resolution" to "in_resolution",
                        "blueness" to "aquamarinity",
                        "identity" to "p0_identity",
                        "gl_FragColor" to "sm_result"
                    )).trim()).toBe(
                    """
                        #line 4
                         
                        vec4 p0_mainFilter(vec4 colorIn) {
                            return mix(colorIn, p0_otherColorStream, p0_fade);
                        }
                    """.trimIndent())
            }

            context("in a patch using a shader channel") {
                val autoWirer by value { AutoWirer(testPlugins()) }
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
                            link("fade", MutableConstPort(".5", GlslType.Float))
                        }
                    }.openForPreview(autoWirer)
                }

                it("accepts color streams from multiple shaders") {
                    kexpect(linkedPatch!!.toFullGlsl("*")).toBe("""
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
                          // Invoke Solid Blue
                          p0_solidBlue_mainImage(p0_solidBluei_result, sm_FragCoord.xy);

                          // Invoke Solid Red
                          p1_solidRed_mainImage(p1_solidRedi_result, sm_FragCoord.xy);

                          // Invoke Fade Filter
                          p2_fadeFilteri_result = p2_fadeFilter_mainFilter(p1_solidRedi_result);

                          sm_result = p2_fadeFilteri_result;
                        }


                    """.trimIndent())
                }
            }

            it("generates invocation GLSL") {
                expect(
                    shader.invocationGlsl(
                        namespace,
                        "resultVar",
                        mapOf("gl_FragColor" to "boof")
                    )
                ).toBe("resultVar = p0_mainFilter(boof)")
            }
        }
    }
})
