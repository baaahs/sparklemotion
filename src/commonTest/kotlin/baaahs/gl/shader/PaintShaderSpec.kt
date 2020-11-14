package baaahs.gl.shader

import baaahs.gl.expects
import baaahs.gl.glsl.GlslAnalyzer
import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.GlslType
import baaahs.gl.override
import baaahs.gl.patch.ContentType
import baaahs.gl.testPlugins
import baaahs.toBeSpecified
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.expect

object PaintShaderSpec : Spek({
    describe("PaintShader") {
        val shaderText by value<String> { toBeSpecified() }
        val shader by value { GlslAnalyzer(testPlugins()).openShader(shaderText) as PaintShader }
        val namespace by value { GlslCode.Namespace("p0") }

        context("generic shaders") {
            override(shaderText) {
                /**language=glsl*/
                """
                    // This Shader's Name
                    // Other stuff.
                    
                    uniform float time;
                    uniform vec2  resolution;
                    uniform vec2  mouse;
                    uniform float blueness;
                    int someGlobalVar;
                    const int someConstVar = 123;
                    
                    float identity(float value) { return value; }

                    void main( void ) {
                        vec2 uv = gl_FragCoord.xy / resolution.xy;
                        gl_FragColor = vec4(uv.xy, identity(blueness), 1.);
                    }
                """.trimIndent()
            }

            it("finds magic uniforms") {
                expects(
                    listOf(
                        InputPort("gl_FragCoord", GlslType.Vec4, "Coordinates", ContentType.UvCoordinateStream),
                        InputPort("time", GlslType.Float, "Time", ContentType.Time),
                        InputPort("resolution", GlslType.Vec2, "Resolution", ContentType.Resolution),
                        InputPort("mouse", GlslType.Vec2, "Mouse", ContentType.Mouse),
                        InputPort("blueness", GlslType.Float, "Blueness")
                    )
                ) { shader.inputPorts.map { it.copy(glslVar = null) } }
            }

            it("generates function declarations") {
                expect(
                    """
                        #line 8
                        int p0_someGlobalVar;
                        
                        #line 9
                        const int p0_someConstVar = 123;
                        
                        #line 11
                        float p0_identity(float value) { return value; }
                        
                        #line 13
                        void p0_main( void ) {
                            vec2 uv = gl_FragCoord.xy / in_resolution.xy;
                            sm_result = vec4(uv.xy, p0_identity(aquamarinity), 1.);
                        }
                    """.trimIndent()
                ) {
                    shader.toGlsl(
                        namespace, mapOf(
                            "resolution" to "in_resolution",
                            "blueness" to "aquamarinity",
                            "identity" to "p0_identity",
                            "gl_FragColor" to "sm_result"
                        )
                    ).trim()
                }
            }

            it("generates invocation GLSL") {
                expect("p0_main()") { shader.invocationGlsl(namespace, "resultVar") }
            }
        }

        context("ShaderToy shaders") {
            override(shaderText) {
                /**language=glsl*/
                """
                    // This Shader's Name
                    // Other stuff.
                    
                    uniform float blueness;
                    int someGlobalVar;
                    const int someConstVar = 123;

                    float identity(float value) { return value; }

                    void mainImage( out vec4 fragColor, in vec2 fragCoord ) {
                        vec2 uv = fragCoord.xy / iResolution.xy * iTime;
                        fragColor = vec4(uv.xy / iMouse, identity(blueness), 1.);
                    }
                """.trimIndent()
            }

            describe("#inputPorts") {
                it("finds magic uniforms") {
                    expects(
                        listOf(
                            InputPort("blueness", GlslType.Float, "Blueness"),
                            InputPort("iResolution", GlslType.Vec3, "Resolution", ContentType.Resolution),
                            InputPort("iTime", GlslType.Float, "Time", ContentType.Time),
                            InputPort("iMouse", GlslType.Vec2, "Mouse", ContentType.Mouse),
                            InputPort("sm_FragCoord", GlslType.Vec2, "Coordinates", ContentType.UvCoordinateStream)
                        )
                    ) { shader.inputPorts.map { it.copy(glslVar = null) } }
                }
            }

            it("generates function declarations") {
                expect(
                    """
                        #line 5
                        int p0_someGlobalVar;
                        
                        #line 6
                        const int p0_someConstVar = 123;
                        
                        #line 8
                        float p0_identity(float value) { return value; }
                        
                        #line 10
                        void p0_mainImage( out vec4 fragColor, in vec2 fragCoord ) {
                            vec2 uv = fragCoord.xy / in_resolution.xy * in_time;
                            fragColor = vec4(uv.xy / in_mouse, p0_identity(aquamarinity), 1.);
                        }
                    """.trimIndent()
                ) {
                    shader.toGlsl(
                        namespace, mapOf(
                            "iResolution" to "in_resolution",
                            "iMouse" to "in_mouse",
                            "iTime" to "in_time",
                            "blueness" to "aquamarinity",
                            "identity" to "p0_identity"
                        )
                    ).trim()
                }
            }

            it("generates invocation GLSL") {
                expect("p0_mainImage(resultVar, sm_FragCoord.xy)") {
                    shader.invocationGlsl(namespace, "resultVar")
                }
            }
        }
    }
})
