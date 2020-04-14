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
                        
                        float identity(float value) { return value; }
    
                        void main( void ) {
                            vec2 uv = gl_FragCoord.xy / resolution.xy;
                            gl_FragColor = vec4(uv.xy, identity(blueness), 1.);
                        }
                        """.trimIndent()
            }

            describe("#toGlsl") {
                it("generates function declarations") {
                    expect(
                        """
                        #line 8
                        float p0_identity(float value) { return value; }
                        
                        #line 10
                        void p0_main( void ) {
                            vec2 uv = gl_FragCoord.xy / resolution.xy;
                            gl_FragColor = vec4(uv.xy, identity(aquamarinity), 1.);
                        }
                        """.trimIndent()
                    ) { shader.toGlsl("p0", mapOf(
                        "blueness" to "aquamarinity",
                        "identity" to "p0_identity"
                    )).trim() }
                }
            }
        }
    }
})