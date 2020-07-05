package baaahs.glshaders

import baaahs.glsl.Shaders.cylindricalUvMapper
import baaahs.show.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.expect

object OpenPatchSpec : Spek({
    describe("GlslProgram") {
        val shaderText by value<String> { TODO() }
        val shader by value { Shader(shaderText) }
        val openShader by value { GlslAnalyzer().asShader(shader) }

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
                val shadersById by value {
                    mapOf(
                        "color" to openShader,
                        "uvShader" to cylindricalUvMapper
                    )
                }
                val dataSourcesById by value {
                    mapOf(
                        "gl_FragCoord" to CorePlugin.ScreenUvCoord(),
                        "uvCoordsTexture" to CorePlugin.UvCoordTexture(),
                        "resolution" to CorePlugin.Resolution(),
                        "time" to CorePlugin.Time(),
                        "bluenessSlider" to CorePlugin.SliderDataSource("Blueness", 0f, 0f, 1f, 0.01f)
                    )
                }

                val openPatch by value {
                    OpenPatch(
                        Patch(
                            listOf(
                                DataSourceRef("gl_FragCoord")
                                        linkTo ShaderInPortRef("color", "gl_FragCoord"),
                                DataSourceRef("resolution")
                                        linkTo ShaderInPortRef("color", "resolution"),
                                DataSourceRef("time")
                                        linkTo ShaderInPortRef("color", "time"),
                                DataSourceRef("bluenessSlider")
                                        linkTo ShaderInPortRef("color", "blueness"),
                                ShaderOutPortRef("color", "gl_FragColor")
                                        linkTo GlslProgram.PixelColor
                            ),
                            Surfaces.AllSurfaces
                        ),
                        shadersById,
                        dataSourcesById
                    )
                }
                val glsl by value { openPatch.toGlsl().trim() }

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
                        
                        // Shader: This Shader's Name; namespace: p0
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
                    override(openPatch) {
                        OpenPatch(
                            Patch(
                                listOf(
                                    DataSourceRef("uvCoordsTexture")
                                            linkTo ShaderInPortRef("uvShader", "uvCoordsTexture"),
                                    ShaderOutPortRef("uvShader", ShaderOutPortRef.ReturnValue)
                                            linkTo ShaderInPortRef("color", "gl_FragCoord"),
                                    DataSourceRef("resolution")
                                            linkTo ShaderInPortRef("color", "resolution"),
                                    DataSourceRef("time")
                                            linkTo ShaderInPortRef("color", "time"),
                                    DataSourceRef("bluenessSlider")
                                            linkTo ShaderInPortRef("color", "blueness"),
                                    ShaderOutPortRef("color", "gl_FragColor")
                                            linkTo GlslProgram.PixelColor
                                ),
                                Surfaces.AllSurfaces
                            ),
                            shadersById, dataSourcesById
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
                            
                            // Shader: Cylindrical Projection; namespace: p0
                            // Cylindrical Projection
                            
                            vec2 p0i_result;
                            
                            #line 6
                            vec2 p0_mainUvFromRaster(vec2 rasterCoord) {
                                int rasterX = int(rasterCoord.x);
                                int rasterY = int(rasterCoord.y);
                                
                                vec2 uvCoord = vec2(
                                    texelFetch(in_uvCoordsTexture, ivec2(rasterX * 2, rasterY), 0).r,    // u
                                    texelFetch(in_uvCoordsTexture, ivec2(rasterX * 2 + 1, rasterY), 0).r // v
                                );
                                return uvCoord;
                            }
                            
                            // Shader: This Shader's Name; namespace: p1
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

