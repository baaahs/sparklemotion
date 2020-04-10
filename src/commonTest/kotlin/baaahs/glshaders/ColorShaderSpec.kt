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

        context("generic shaders") {
            override(shaderText) {
                /**language=glsl*/
                """
                        // This Shader's Name
                        // Other stuff.
                        
                        uniform float time;
                        uniform vec2  resolution;
                        uniform float blueness;
    
                        void main( void ) {
                            vec2 uv = gl_FragCoord.xy / resolution.xy;
                            gl_FragColor = vec4(uv.xy, 0., 1.);
                        }
                        """.trimIndent()
            }

            describe("#toGlsl") {
                it("generates text") {
                    expect(
                        "\n" +
                                """
                                #line 1
                                // This Shader's Name
                                // Other stuff.
                                
                                uniform float p0_time;
                                #line 5
                                uniform vec2  p0_resolution;
                                #line 6
                                uniform float p0_blueness;
                                #line 8
                                void p0_main( void ) {
                                    vec2 uv = p0_gl_FragCoord.xy / p0_resolution.xy;
                                    p0_gl_FragColor = vec4(uv.xy, 0., 1.);
                                }
                                """.trimIndent()
                    ) { shader.toGlsl("p0") }
                }
            }
        }
    }
})