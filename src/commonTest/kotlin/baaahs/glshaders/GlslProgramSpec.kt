package baaahs.glshaders

import baaahs.glshaders.GlslProgram.*
import baaahs.glsl.GlslBase
import baaahs.testing.override
import baaahs.testing.value
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.expect

object GlslProgramSpec : Spek({
    describe("GlslProgram") {
        val gl by value { GlslBase.manager.createContext() }
        val shaderText by value<String> { TODO() }
        val shader by value { GlslAnalyzer().asShader(shaderText) as ShaderFragment.ColorShader }

        context("GLSL generation") {
            override(shaderText) {
                /**language=glsl*/
                """
                // This Shader's Name
                // Other stuff.
                
                uniform float time;
                uniform vec2  resolution;
                uniform float blueness;
                int someGlobalVar;
                const int someConstVar = 123;
                
                int anotherFunc(int i) { return i; }
                
                void main( void ) {
                    vec2 uv = gl_FragCoord.xy / resolution.xy;
                    someGlobalVar = anotherFunc(someConstVar);
                    gl_FragColor = vec4(uv.xy, blueness, 1.);
                }
                """.trimIndent()
            }

            describe("#toGlsl") {
                val patch by value {
                    Patch(
                        mapOf("color" to shader),
                        listOf(
                            UvCoord to ShaderPort("color", "gl_FragCoord"),
                            Resolution to ShaderPort("color", "resolution"),
                            Time to ShaderPort("color", "time"),
                            UniformInput("float", "blueness") to ShaderPort("color", "blueness"),
                            ShaderPort("color", "gl_FragColor") to PixelColor
                        )
                    )
                }
                val glsl by value { patch.toGlsl() }

                it("generates GLSL") {
                    expect(
                        """
                        #ifdef GL_ES
                        precision mediump float;
                        #endif
                        
                        // SparkleMotion generated GLSL

                        layout(location = 0) out vec4 sm_pixelColor;

                        uniform vec2 in_resolution;
                        uniform float in_time;
                        uniform float in_blueness;
                        
                        // Shader ID: color; namespace: p0
                        // This Shader's Name
                        
                        #line 7
                        int p0_someGlobalVar;
                        
                        #line 8
                        const int p0_someConstVar = 123;
                        
                        #line 10
                        int p0_anotherFunc(int i) { return i; }
                        
                        #line 12
                        void p0_main( void ) {
                            vec2 uv = gl_FragCoord.xy / in_resolution.xy;
                            p0_someGlobalVar = p0_anotherFunc(p0_someConstVar);
                            sm_pixelColor = vec4(uv.xy, in_blueness, 1.);
                        }


                        #line 10001
                        void main() {
                          p0_main();
                        }
                        """.trimIndent()
                    ) { glsl.trim() }
                }
            }
        }
    }
})