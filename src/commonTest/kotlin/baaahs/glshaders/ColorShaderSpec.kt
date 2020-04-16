package baaahs.glshaders

import baaahs.testing.override
import baaahs.testing.value
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.expect

object ColorShaderSpec : Spek({
    describe("ColorShader") {
        val shaderText by value<String> { TODO() }
        val shader by value { GlslAnalyzer().asShader(shaderText) as ShaderFragment.ColorShader }
        val namespace by value { GlslCode.Namespace("p0") }

        context("generic shaders") {
            override(shaderText) {
                /**language=glsl*/
                """
                        // This Shader's Name
                        // Other stuff.
                        
                        uniform float time;
                        uniform vec2  resolution;
                        uniform float blueness;
                        
                        float identity(float value) { return value; }
    
                        void main( void ) {
                            vec2 uv = gl_FragCoord.xy / resolution.xy;
                            gl_FragColor = vec4(uv.xy, identity(blueness), 1.);
                        }
                        """.trimIndent()
            }

            it("generates function declarations") {
                expect(
                    """
                        #line 8
                        float p0_identity(float value) { return value; }
                        
                        #line 10
                        void p0_main( void ) {
                            vec2 uv = gl_FragCoord.xy / in_resolution.xy;
                            sm_pixelColor = vec4(uv.xy, p0_identity(aquamarinity), 1.);
                        }
                        """.trimIndent()
                ) { shader.toGlsl(
                    namespace, mapOf(
                        "resolution" to "in_resolution",
                        "blueness" to "aquamarinity",
                        "identity" to "p0_identity",
                        "gl_FragColor" to "sm_pixelColor"
                )).trim() }
            }

            it("generates invocation GLSL") {
                expect("  p0_main();\n") { shader.invocationGlsl(namespace) }
            }
        }

        context("ShaderToy shaders") {
            override(shaderText) {
                /**language=glsl*/
                """
                        // This Shader's Name
                        // Other stuff.
                        
                        uniform float iTime;
                        uniform vec2  iResolution;
                        uniform vec2  iMouse;
                        uniform float blueness;
                        
                        float identity(float value) { return value; }
    
                        void mainImage( out vec4 fragColor, in vec2 fragCoord ) {
                            vec2 uv = fragCoord.xy / iResolution.xy;
                            fragColor = vec4(uv.xy / iMouse, identity(blueness), 1.);
                        }
                        """.trimIndent()
            }

            describe("#toGlsl") {
                it("generates function declarations") {
                    expect(
                        """
                        #line 9
                        float p0_identity(float value) { return value; }
                        
                        #line 11
                        void p0_mainImage( out vec4 fragColor, in vec2 fragCoord ) {
                            vec2 uv = fragCoord.xy / in_resolution.xy;
                            fragColor = vec4(uv.xy / in_mouse, p0_identity(aquamarinity), 1.);
                        }
                        """.trimIndent()
                    ) { shader.toGlsl(
                        namespace, mapOf(
                        "iResolution" to "in_resolution",
                        "iMouse" to "in_mouse",
                        "blueness" to "aquamarinity",
                        "identity" to "p0_identity"
                    )).trim() }
                }

                it("generates invocation GLSL") {
                    expect("  p0_mainImage(sm_pixelColor, gl_FragCoord.xy);\n") { shader.invocationGlsl(namespace) }
                }
            }
        }
    }
})