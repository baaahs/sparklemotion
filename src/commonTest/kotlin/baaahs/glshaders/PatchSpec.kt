package baaahs.glshaders

import baaahs.glsl.GlslRenderer
import baaahs.ports.ShaderInPortRef
import baaahs.ports.ShaderOutPortRef
import baaahs.ports.inputPortRef
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.expect

object PatchSpec : Spek({
    describe("GlslProgram") {
        val shaderText by value<String> { TODO() }
        val shader by value { GlslAnalyzer().asShader(shaderText) as ColorShader }

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
                            GlslProgram.GlFragCoord
                                    linkTo ShaderInPortRef("color", "gl_FragCoord"),
                            GlslProgram.Resolution
                                    linkTo ShaderInPortRef("color", "resolution"),
                            GlslProgram.Time
                                    linkTo ShaderInPortRef("color", "time"),
                            inputPortRef("bluenessSlider", "float", "Blueness")
                                    linkTo ShaderInPortRef("color", "blueness"),
                            ShaderInPortRef("color", "gl_FragColor")
                                    linkTo GlslProgram.PixelColor
                        )
                    )
                }
                val glsl by value { patch.toGlsl().trim() }

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
                        uniform float in_bluenessSlider;
                        
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
                            sm_pixelColor = vec4(uv.xy, in_bluenessSlider, 1.);
                        }


                        #line 10001
                        void main() {
                          p0_main();
                        }
                        """.trimIndent()
                    ) { glsl }
                }

                context("with UV translation shader") {
                    val uvShaderSrc by value {
                        /**language=glsl*/
                        """
                            uniform sampler2D uvCoordsTexture;
                            
                            vec2 mainUvFromRaster(vec2 rasterCoord) {
                                int rasterX = int(rasterCoord.x);
                                int rasterY = int(rasterCoord.y);
                                
                                vec2 uvCoord = vec2(
                                    texelFetch(uvCoordsTexture, ivec2(rasterX * 2, rasterY), 0).r,    // u
                                    texelFetch(uvCoordsTexture, ivec2(rasterX * 2 + 1, rasterY), 0).r // v
                                );
                                return uvCoord;
                            }
                        """.trimIndent()
                    }
                    val uvShaderFragment = GlslRenderer.uvMapper
                    override(patch) {
                        Patch(
                            mapOf(
                                "uv" to uvShaderFragment,
                                "color" to shader
                            ),
                            listOf(
                                inputPortRef("uvCoordsTexture", "sampler2D", "U/V Coords Texture", "baaahs.Core:uvCoordsTexture")
                                        linkTo ShaderInPortRef("uv", "uvCoordsTexture"),
                                ShaderOutPortRef("uv")
                                        linkTo ShaderInPortRef("color", "gl_FragCoord"),
                                GlslProgram.Resolution
                                        linkTo ShaderInPortRef("color", "resolution"),
                                GlslProgram.Time
                                        linkTo ShaderInPortRef("color", "time"),
                                inputPortRef("bluenessSlider", "float", "Blueness")
                                        linkTo ShaderInPortRef("color", "blueness")
                            )
                        )
                    }

                    it("generates GLSL") {
                        expect(
                            /**language=glsl*/
                            """
                            #ifdef GL_ES
                            precision mediump float;
                            #endif
                            
                            // SparkleMotion generated GLSL
    
                            layout(location = 0) out vec4 sm_pixelColor;
    
                            uniform sampler2D in_uvCoordsTexture;
                            uniform vec2 in_resolution;
                            uniform float in_time;
                            uniform float in_bluenessSlider;
                            
                            // Shader ID: uv; namespace: p0
                            // Unknown Shader
                            
                            vec2 p0i_result;
                            
                            #line 3
                            vec2 p0_mainUvFromRaster(vec2 rasterCoord) {
                                int rasterX = int(rasterCoord.x);
                                int rasterY = int(rasterCoord.y);
                                
                                vec2 uvCoord = vec2(
                                    texelFetch(in_uvCoordsTexture, ivec2(rasterX * 2, rasterY), 0).r,    // u
                                    texelFetch(in_uvCoordsTexture, ivec2(rasterX * 2 + 1, rasterY), 0).r // v
                                );
                                return uvCoord;
                            }
                            
                            // Shader ID: color; namespace: p1
                            // This Shader's Name
                            
                            #line 7
                            int p1_someGlobalVar;
                            
                            #line 8
                            const int p1_someConstVar = 123;
                            
                            #line 10
                            int p1_anotherFunc(int i) { return i; }
                            
                            #line 12
                            void p1_main( void ) {
                                vec2 uv = p0i_result.xy / in_resolution.xy;
                                p1_someGlobalVar = p1_anotherFunc(p1_someConstVar);
                                sm_pixelColor = vec4(uv.xy, in_bluenessSlider, 1.);
                            }
    
    
                            #line 10001
                            void main() {
                              p0i_result = p0_mainUvFromRaster(gl_FragCoord.xy);
                              p1_main();
                            }
                            """.trimIndent()
                        ) { glsl }
                    }
                }
            }
        }
    }
})

