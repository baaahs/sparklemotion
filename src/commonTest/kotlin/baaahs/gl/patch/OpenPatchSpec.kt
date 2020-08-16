package baaahs.gl.patch

import baaahs.gl.override
import baaahs.glsl.Shaders.cylindricalProjection
import baaahs.plugin.CorePlugin
import baaahs.plugin.Plugins
import baaahs.show.ShaderChannel
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutableShaderOutPort
import baaahs.toBeSpecified
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.expect

object OpenPatchSpec : Spek({
    describe("GlslProgram") {
        val shaderText by value<String> { toBeSpecified() }
        val autoWirer by value { AutoWirer(Plugins.safe()) }
        val glslAnalyzer by value { autoWirer.glslAnalyzer }
        val shader by value { glslAnalyzer.import(shaderText) }
        val openShader by value { glslAnalyzer.openShader(shader) }

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
                val linkedPatch by value {
                    MutablePatch {
                        addShaderInstance(shader) {
                            link("gl_FragCoord", CorePlugin.ScreenUvCoord())
                            link("resolution", CorePlugin.Resolution())
                            link("time", CorePlugin.Time())
                            link("blueness",
                                CorePlugin.SliderDataSource(
                                    "Blueness",
                                    0f,
                                    0f,
                                    1f,
                                    null
                                )
                            )
                            shaderChannel = ShaderChannel.Main
                        }
                    }.openForPreview(autoWirer)!!
                }
                val glsl by value {
                    linkedPatch.toGlsl().trim()
                }

                it("generates GLSL") {
                    expect(
                        """
                            #ifdef GL_ES
                            precision mediump float;
                            #endif

                            // SparkleMotion-generated GLSL

                            layout(location = 0) out vec4 sm_result;

                            uniform float in_bluenessSlider;
                            uniform vec2 in_resolution;
                            uniform float in_time;

                            // Shader: This Shader's Name; namespace: p0
                            // This Shader's Name

                            vec4 p0_thisShaderSName_gl_FragColor = vec4(0., 0., 0., 1.);

                            #line 7
                            int p0_thisShaderSName_someGlobalVar;

                            #line 8
                            const int p0_thisShaderSName_someConstVar = 123;

                            #line 10
                            int p0_thisShaderSName_anotherFunc(int i) { return i; }

                            #line 12
                            void p0_thisShaderSName_main( void ) {
                                vec2 uv = gl_FragCoord.xy / in_resolution.xy;
                                p0_thisShaderSName_someGlobalVar = p0_thisShaderSName_anotherFunc(p0_thisShaderSName_someConstVar);
                                p0_thisShaderSName_gl_FragColor = vec4(uv.xy, in_bluenessSlider, 1.);
                            }


                            #line -1
                            void main() {
                              p0_thisShaderSName_main(); // This Shader's Name
                              sm_result = p0_thisShaderSName_gl_FragColor;
                            }
                        """.trimIndent()
                    ) { glsl }
                }

                context("with UV translation shader") {
                    override(linkedPatch) {
                        MutablePatch {
                            addShaderInstance(cylindricalProjection) {
                                link("pixelCoordsTexture",
                                    CorePlugin.PixelCoordsTexture()
                                )
                                link("modelInfo",
                                    CorePlugin.ModelInfoDataSource()
                                )
                                shaderChannel = ShaderChannel.Main
                            }

                            addShaderInstance(openShader.shader) {
                                link(
                                    "gl_FragCoord",
                                    MutableShaderOutPort(
                                        findShaderInstanceFor(cylindricalProjection)
                                    )
                                )
                                link("resolution", CorePlugin.Resolution())
                                link("time", CorePlugin.Time())
                                link("blueness",
                                    CorePlugin.SliderDataSource(
                                        "Blueness",
                                        0f,
                                        0f,
                                        1f,
                                        null
                                    )
                                )
                                shaderChannel = ShaderChannel.Main
                            }
                        }.openForPreview(autoWirer)!!
                    }

                    it("generates GLSL") {
                        expect(
                            /**language=glsl*/
                            """
                                #ifdef GL_ES
                                precision mediump float;
                                #endif

                                // SparkleMotion-generated GLSL

                                layout(location = 0) out vec4 sm_result;

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

                                vec2 p0_cylindricalProjectioni_result = vec2(0.);

                                #line 12
                                const float p0_cylindricalProjection_PI = 3.141592654;

                                #line 14
                                vec2 p0_cylindricalProjection_project(vec3 pixelLocation) {
                                    vec3 pixelOffset = pixelLocation - in_modelInfo.center;
                                    vec3 normalDelta = normalize(pixelOffset);
                                    float theta = atan(abs(normalDelta.z), normalDelta.x); // theta in range [-π,π]
                                    if (theta < 0.0) theta += (2.0f * p0_cylindricalProjection_PI);                 // theta in range [0,2π)
                                    float u = theta / (2.0f * p0_cylindricalProjection_PI);                         // u in range [0,1)
                                    float v = (pixelOffset.y + in_modelInfo.extents.y / 2.0f) / in_modelInfo.extents.y;
                                    return vec2(u, v);
                                }

                                #line 24
                                vec2 p0_cylindricalProjection_mainProjection(vec2 rasterCoord) {
                                    int rasterX = int(rasterCoord.x);
                                    int rasterY = int(rasterCoord.y);
                                    
                                    vec3 pixelCoord = texelFetch(in_pixelCoordsTexture, ivec2(rasterX, rasterY), 0).xyz;
                                    return p0_cylindricalProjection_project(pixelCoord);
                                }

                                // Shader: This Shader's Name; namespace: p1
                                // This Shader's Name

                                vec4 p1_thisShaderSName_gl_FragColor = vec4(0., 0., 0., 1.);

                                #line 7
                                int p1_thisShaderSName_someGlobalVar;

                                #line 8
                                const int p1_thisShaderSName_someConstVar = 123;

                                #line 10
                                int p1_thisShaderSName_anotherFunc(int i) { return i; }

                                #line 12
                                void p1_thisShaderSName_main( void ) {
                                    vec2 uv = p0_cylindricalProjectioni_result.xy / in_resolution.xy;
                                    p1_thisShaderSName_someGlobalVar = p1_thisShaderSName_anotherFunc(p1_thisShaderSName_someConstVar);
                                    p1_thisShaderSName_gl_FragColor = vec4(uv.xy, in_bluenessSlider, 1.);
                                }


                                #line -1
                                void main() {
                                  p0_cylindricalProjectioni_result = p0_cylindricalProjection_mainProjection(gl_FragCoord.xy); // Cylindrical Projection
                                  p1_thisShaderSName_main(); // This Shader's Name
                                  sm_result = p1_thisShaderSName_gl_FragColor;
                                }
                            """.trimIndent()
                        ) { glsl }
                    }
                }
            }
        }
    }
})

