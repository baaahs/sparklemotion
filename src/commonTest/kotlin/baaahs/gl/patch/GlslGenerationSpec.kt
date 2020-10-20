package baaahs.gl.patch

import baaahs.gl.override
import baaahs.glsl.Shaders.cylindricalProjection
import baaahs.plugin.CorePlugin
import baaahs.plugin.Plugins
import baaahs.show.ShaderChannel
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutableShaderChannel
import baaahs.show.mutable.MutableShaderOutPort
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.expect

object GlslGenerationSpec : Spek({
    describe("Generation of GLSL from patches") {
        val shaderText by value {
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
        val autoWirer by value { AutoWirer(Plugins.safe()) }
        val glslAnalyzer by value { autoWirer.glslAnalyzer }
        val mainShader by value { glslAnalyzer.import(shaderText) }
        val mutablePatch by value { MutablePatch { } }
        val linkedPatch by value { mutablePatch.openForPreview(autoWirer)!! }
        val glsl by value { linkedPatch.toGlsl().trim() }

        context("with screen coordinates for preview") {
            beforeEachTest {
                mutablePatch.addShaderInstance(mainShader) {
                    link("gl_FragCoord", CorePlugin.ScreenUvCoordDataSource())
                    link("resolution", CorePlugin.ResolutionDataSource())
                    link("time", CorePlugin.TimeDataSource())
                    link(
                        "blueness",
                        CorePlugin.SliderDataSource("Blueness", 0f, 0f, 1f, null)
                    )
                    shaderChannel = MutableShaderChannel(ShaderChannel.Main.id)
                }
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


                        #line 10001
                        void main() {
                          p0_thisShaderSName_main(); // This Shader's Name
                          sm_result = p0_thisShaderSName_gl_FragColor;
                        }
                    """.trimIndent()
                ) { glsl }
            }
        }

        context("with a ShaderToy paint shader") {
            override(shaderText) {
                /**language=glsl*/
                """
                    // This Shader's Name
                    // Other stuff.
                    
                    uniform float blueness;
                    int someGlobalVar;
                    const int someConstVar = 123;
                    
                    int anotherFunc(int i) { return i; }
                    
                    void mainImage( out vec4 fragColor, in vec2 fragCoord ) {
                        vec2 uv = fragCoord.xy / iResolution.xy;
                        someGlobalVar = anotherFunc(someConstVar) + iTime * 0.;
                        fragColor = vec4(uv.xy, blueness, 1.);
                    }
                """.trimIndent()
            }

            beforeEachTest {
                mutablePatch.addShaderInstance(mainShader) {
                    link(
                        "blueness",
                        CorePlugin.SliderDataSource("Blueness", 0f, 0f, 1f, null)
                    )
                    link("iResolution", CorePlugin.ResolutionDataSource())
                    link("iTime", CorePlugin.TimeDataSource())
                    link("sm_FragCoord", CorePlugin.ScreenUvCoordDataSource())
                    shaderChannel = MutableShaderChannel(ShaderChannel.Main.id)
                }
            }

            it("generates GLSL") {
                linkedPatch.shaderInstance.incomingLinks.forEach { (port, link) ->
                    println("port $port -> $link")
                }
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

                        vec4 p0_thisShaderSNamei_result = vec4(0., 0., 0., 1.);

                        #line 5
                        int p0_thisShaderSName_someGlobalVar;

                        #line 6
                        const int p0_thisShaderSName_someConstVar = 123;

                        #line 8
                        int p0_thisShaderSName_anotherFunc(int i) { return i; }

                        #line 10
                        void p0_thisShaderSName_mainImage( out vec4 fragColor, in vec2 fragCoord ) {
                            vec2 uv = fragCoord.xy / in_resolution.xy;
                            p0_thisShaderSName_someGlobalVar = p0_thisShaderSName_anotherFunc(p0_thisShaderSName_someConstVar) + in_time * 0.;
                            fragColor = vec4(uv.xy, in_bluenessSlider, 1.);
                        }


                        #line 10001
                        void main() {
                          p0_thisShaderSName_mainImage(p0_thisShaderSNamei_result, gl_FragCoord.xy); // This Shader's Name
                          sm_result = p0_thisShaderSNamei_result;
                        }
                    """.trimIndent()
                ) { glsl }
            }
        }

        describe("with projection shader") {
            beforeEachTest {
                mutablePatch.apply {
                    addShaderInstance(cylindricalProjection) {
                        link(
                            "pixelCoordsTexture",
                            CorePlugin.PixelCoordsTextureDataSource()
                        )
                        link(
                            "modelInfo",
                            CorePlugin.ModelInfoDataSource()
                        )
                        shaderChannel = MutableShaderChannel(ShaderChannel.Main.id)
                    }

                    addShaderInstance(mainShader) {
                        link(
                            "gl_FragCoord",
                            MutableShaderOutPort(
                                findShaderInstanceFor(cylindricalProjection)
                            )
                        )
                        link("resolution", CorePlugin.ResolutionDataSource())
                        link("time", CorePlugin.TimeDataSource())
                        link(
                            "blueness",
                            CorePlugin.SliderDataSource(
                                "Blueness",
                                0f,
                                0f,
                                1f,
                                null
                            )
                        )
                        shaderChannel = MutableShaderChannel(ShaderChannel.Main.id)
                    }
                }
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


                        #line 10001
                        void main() {
                          p0_cylindricalProjectioni_result = p0_cylindricalProjection_mainProjection(gl_FragCoord.xy); // Cylindrical Projection
                          p1_thisShaderSName_main(); // This Shader's Name
                          sm_result = p1_thisShaderSName_gl_FragColor;
                        }
                    """.trimIndent()
                ) { glsl }
            }
        }

        describe("mixing from another channel") {
            val otherShaderActualChannel by value { "other" }

            override(shaderText) {
                """
                    // Cross-fade shader
                    varying vec4 inColor2; // @type color-stream
                    uniform float fade;

                    vec4 mainFilter(vec4 inColor) {
                        return mix(inColor, inColor2, fade);
                    }
                """.trimIndent()
            }

            val mainPaintShader by value {
                glslAnalyzer.import(
                    """
                    void main( void ) {
                        gl_FragColor = vec4(1., 0., 0., 1.);
                    }
                """.trimIndent(), "Main Paint Shader"
                )
            }

            val otherPaintShader by value {
                glslAnalyzer.import(
                    """
                    void mainImage( out vec4 fragColor, in vec2 fragCoord ) {
                        fragColor = vec4(0., 1., 0., 1.);
                    }
                """.trimIndent(), "Other Paint Shader"
                )
            }

            beforeEachTest {
                mutablePatch.addShaderInstance(mainPaintShader)
                mutablePatch.addShaderInstance(otherPaintShader) {
                    shaderChannel = MutableShaderChannel.from(otherShaderActualChannel)
                }

                mutablePatch.addShaderInstance(mainShader) {
                    link("fade", CorePlugin.SliderDataSource("Fade", 0f, 0f, 1f, null))
                    link("gl_FragColor", MutableShaderChannel("main"))
                    link("inColor2", MutableShaderChannel("other"))
                }
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

                        uniform float in_fadeSlider;

                        // Shader: Main Paint Shader; namespace: p0
                        // Main Paint Shader

                        vec4 p0_mainPaintShader_gl_FragColor = vec4(0., 0., 0., 1.);

                        #line 1
                        void p0_mainPaintShader_main( void ) {
                            p0_mainPaintShader_gl_FragColor = vec4(1., 0., 0., 1.);
                        }

                        // Shader: Other Paint Shader; namespace: p1
                        // Other Paint Shader

                        vec4 p1_otherPaintShaderi_result = vec4(0., 0., 0., 1.);

                        #line 1
                        void p1_otherPaintShader_mainImage( out vec4 fragColor, in vec2 fragCoord ) {
                            fragColor = vec4(0., 1., 0., 1.);
                        }

                        // Shader: Cross-fade shader; namespace: p2
                        // Cross-fade shader

                        vec4 p2_crossFadeShaderi_result = vec4(0., 0., 0., 1.);

                        #line 5
                        vec4 p2_crossFadeShader_mainFilter(vec4 inColor) {
                            return mix(inColor, p1_otherPaintShaderi_result, in_fadeSlider);
                        }


                        #line 10001
                        void main() {
                          p0_mainPaintShader_main(); // Main Paint Shader
                          p1_otherPaintShader_mainImage(p1_otherPaintShaderi_result, sm_FragCoord.xy); // Other Paint Shader
                          p2_crossFadeShaderi_result = p2_crossFadeShader_mainFilter(p0_mainPaintShader_gl_FragColor); // Cross-fade shader
                          sm_result = p2_crossFadeShaderi_result;
                        }
                    """.trimIndent()
                ) { glsl }
            }

            context("when there's no paint shader on the other channel") {
                override(otherShaderActualChannel) { "notOther" }

                it("should report an error to the user BUT IT CURRENTLY DOESNT") {
                    // TODO: WRONG WRONG WRONG we should tell the user that something's wrong?
                    expect(
                        /**language=glsl*/
                        """
                            #ifdef GL_ES
                            precision mediump float;
                            #endif

                            // SparkleMotion-generated GLSL

                            layout(location = 0) out vec4 sm_result;

                            uniform float in_fadeSlider;

                            // Shader: Main Paint Shader; namespace: p0
                            // Main Paint Shader

                            vec4 p0_mainPaintShader_gl_FragColor = vec4(0., 0., 0., 1.);

                            #line 1
                            void p0_mainPaintShader_main( void ) {
                                p0_mainPaintShader_gl_FragColor = vec4(1., 0., 0., 1.);
                            }

                            // Shader: Cross-fade shader; namespace: p1
                            // Cross-fade shader

                            vec4 p1_crossFadeShaderi_result = vec4(0., 0., 0., 1.);

                            #line 5
                            vec4 p1_crossFadeShader_mainFilter(vec4 inColor) {
                                return mix(inColor, p1_crossFadeShader_inColor2, in_fadeSlider);
                            }


                            #line 10001
                            void main() {
                              p0_mainPaintShader_main(); // Main Paint Shader
                              p1_crossFadeShaderi_result = p1_crossFadeShader_mainFilter(p0_mainPaintShader_gl_FragColor); // Cross-fade shader
                              sm_result = p1_crossFadeShaderi_result;
                            }
                        """.trimIndent()
                    ) { glsl }
                }
            }
        }
    }
})

