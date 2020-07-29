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
                
                uniform float blueness;
                uniform vec2  resolution;
                uniform float time;
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
                val openPatch by value {
                    PatchEditor {
                        addShaderInstance(shader) {
                            link("gl_FragCoord", CorePlugin.ScreenUvCoord())
                            link("resolution", CorePlugin.Resolution())
                            link("time", CorePlugin.Time())
                            link("blueness", CorePlugin.SliderDataSource("Blueness", 0f, 0f, 1f, null))
                            role = ShaderRole.Paint
                        }
                    }.open()
                }
                val glsl by value {
                    openPatch.toGlsl().trim()
                }

                it("generates GLSL") {
                    expect(
                        """
                        #ifdef GL_ES
                        precision mediump float;
                        #endif
                        
                        // SparkleMotion generated GLSL

                        layout(location = 0) out vec4 sm_pixelColor;

                        uniform float in_bluenessSlider;
                        uniform vec2 in_resolution;
                        uniform float in_time;
                        
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


                        #line -1
                        void main() {
                          p0_main();
                        }
                        """.trimIndent()
                    ) { glsl }
                }

                context("with UV translation shader") {
                    override(openPatch) {
                        PatchEditor {
                            addShaderInstance(cylindricalUvMapper.shader) {
                                link("pixelCoordsTexture", CorePlugin.PixelCoordsTexture())
                                link("modelInfo", CorePlugin.ModelInfoDataSource("ModelInfo"))
                                role = ShaderRole.Projection
                            }

                            addShaderInstance(openShader.shader) {
                                link("gl_FragCoord", ShaderOutPortEditor(findShaderInstanceFor(cylindricalUvMapper.shader), ShaderOutPortRef.ReturnValue))
                                link("resolution", CorePlugin.Resolution())
                                link("time", CorePlugin.Time())
                                link("blueness", CorePlugin.SliderDataSource("Blueness", 0f, 0f, 1f, null))
                                role = ShaderRole.Paint
                            }
                        }.open()
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

                                struct ModelInfo {
                                    vec3 center;
                                    vec3 extents;
                                };
                                uniform float in_bluenessSlider;
                                uniform ModelInfo in_modelInfo;
                                uniform sampler2D in_pixelCoordsTexture;
                                uniform vec2 in_resolution;
                                uniform float in_time;

                                // Shader: Cylindrical Projection; namespace: p0
                                // Cylindrical Projection

                                vec2 p0i_result;

                                #line 12
                                const float p0_PI = 3.141592654;

                                #line 14
                                vec2 p0_project(vec3 pixelLocation) {
                                    vec3 pixelOffset = pixelLocation - in_modelInfo.center;
                                    vec3 normalDelta = normalize(pixelOffset);
                                    float theta = atan(abs(normalDelta.z), normalDelta.x); // theta in range [-π,π]
                                    if (theta < 0.0) theta += (2.0f * p0_PI);                 // theta in range [0,2π)
                                    float u = theta / (2.0f * p0_PI);                         // u in range [0,1)
                                    float v = (pixelOffset.y + in_modelInfo.extents.y / 2.0f) / in_modelInfo.extents.y;
                                    return vec2(u, v);
                                }

                                #line 24
                                vec2 p0_mainUvFromRaster(vec2 rasterCoord) {
                                    int rasterX = int(rasterCoord.x);
                                    int rasterY = int(rasterCoord.y);
                                    
                                    vec3 pixelCoord = texelFetch(in_pixelCoordsTexture, ivec2(rasterX, rasterY), 0).xyz;
                                    return p0_project(pixelCoord);
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


                                #line -1
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

