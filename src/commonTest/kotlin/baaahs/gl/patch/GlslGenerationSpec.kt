package baaahs.gl.patch

import baaahs.device.PixelLocationFeed
import baaahs.gl.glsl.GlslType
import baaahs.gl.kexpect
import baaahs.gl.override
import baaahs.gl.patch.ContentType.Companion.Color
import baaahs.gl.testToolchain
import baaahs.glsl.Shaders.cylindricalProjection
import baaahs.plugin.core.FixtureInfoFeed
import baaahs.plugin.core.MovingHeadParams
import baaahs.plugin.core.datasource.*
import baaahs.show.Stream
import baaahs.show.mutable.*
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.fail

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
                int someGlobalVar = int(blueness * 100.);
                int anotherGlobalVar = someGlobalVar + 1;
                const int someConstVar = 123;
                
                int anotherFunc(int i) { return i; }
                
                void main( void ) {
                    vec2 uv = gl_FragCoord.xy / resolution.xy;
                    someGlobalVar = anotherFunc(someConstVar);
                    gl_FragColor = vec4(uv.xy, blueness, 1.);
                }
            """.trimIndent()
        }
        val mainShader by value { testToolchain.import(shaderText) }
        val mutablePatchSet by value { MutablePatchSet() }
        val resultContentType by value { Color }
        val linkedPatch by value {
            mutablePatchSet.openForPreview(testToolchain, resultContentType)
                ?: fail("openForPreview returned null, maybe no shaders on mutablePatch?")
        }
        val glsl by value { linkedPatch.toGlsl().trim() }

        context("with screen coordinates for preview") {
            beforeEachTest {
                mutablePatchSet.addPatch(mainShader) {
                    link("fragCoord", RasterCoordinateFeed())
                    link("resolution", ResolutionFeed())
                    link("time", TimeFeed())
                    link(
                        "blueness",
                        SliderFeed("Blueness", 0f, 0f, 1f, null)
                    )
                    stream = Stream.Main.editor()
                }
            }

            it("generates GLSL") {
                kexpect(glsl).toBe(
                    /**language=glsl*/
                    """
                        #ifdef GL_ES
                        precision mediump float;
                        #endif

                        // SparkleMotion-generated GLSL

                        layout(location = 0) out vec4 sm_result;

                        // Feed: Blueness Slider
                        uniform float in_bluenessSlider;

                        // Feed: Resolution
                        uniform vec2 in_resolution;

                        // Feed: Time
                        uniform float in_time;

                        // Shader: This Shader's Name; namespace: p0
                        // This Shader's Name

                        vec4 p0_thisShaderSName_gl_FragColor = vec4(0., 0., 0., 1.);

                        #line 7 0
                        int p0_thisShaderSName_someGlobalVar;

                        #line 8 0
                        int p0_thisShaderSName_anotherGlobalVar;

                        #line 9 0
                        const int p0_thisShaderSName_someConstVar = 123;

                        #line 11 0
                        int p0_thisShaderSName_anotherFunc(int i) { return i; }

                        #line 13 0
                        void p0_thisShaderSName_main( void ) {
                            vec2 uv = gl_FragCoord.xy / in_resolution.xy;
                            p0_thisShaderSName_someGlobalVar = p0_thisShaderSName_anotherFunc(p0_thisShaderSName_someConstVar);
                            p0_thisShaderSName_gl_FragColor = vec4(uv.xy, in_bluenessSlider, 1.);
                        }

                        void p0_thisShaderSNamei_init() {    
                        #line 7 0
                          p0_thisShaderSName_someGlobalVar = int(in_bluenessSlider * 100.);
                            
                        #line 8 0
                          p0_thisShaderSName_anotherGlobalVar = p0_thisShaderSName_someGlobalVar + 1;
                        }


                        #line 10001
                        void main() {
                            // Init This Shader's Name.
                            p0_thisShaderSNamei_init();

                            // Invoke This Shader's Name
                            p0_thisShaderSName_main();

                            sm_result = p0_thisShaderSName_gl_FragColor;
                        }
                    """.trimIndent()
                )
            }
        }

        context("with param input ports") {
            override(shaderText) {
                /**language=glsl*/
                """
                    uniform vec2  resolution;
                    vec2 anotherFunc(vec2 fragCoord) { return fragCoord; }
                    
                    // @param fragCoord uv-coordinate
                    void main(vec2 fragCoord) {
                        vec2 uv = anotherFunc(fragCoord) / resolution.xy;
                        gl_FragColor = vec4(uv.xy, 0., 1.);
                    }
                """.trimIndent()
            }

            beforeEachTest {
                mutablePatchSet.addPatch(mainShader) {
                    link("resolution", ResolutionFeed())
                    link("fragCoord", RasterCoordinateFeed())
                    stream = Stream.Main.editor()
                }
            }

            it("generates GLSL") {
                kexpect(glsl).toBe(
                    /**language=glsl*/
                    """
                        #ifdef GL_ES
                        precision mediump float;
                        #endif

                        // SparkleMotion-generated GLSL

                        layout(location = 0) out vec4 sm_result;

                        // Feed: Raster Coordinate
                        uniform vec2 ds_rasterCoordinate_offset;
                        vec4 in_rasterCoordinate;

                        // Feed: Resolution
                        uniform vec2 in_resolution;

                        // Shader: Untitled Shader; namespace: p0
                        // Untitled Shader

                        vec4 p0_untitledShader_gl_FragColor = vec4(0., 0., 0., 1.);

                        #line 2 0
                        vec2 p0_untitledShader_anotherFunc(vec2 fragCoord) { return fragCoord; }

                        #line 5 0
                        void p0_untitledShader_main(vec2 fragCoord) {
                            vec2 uv = p0_untitledShader_anotherFunc(fragCoord) / in_resolution.xy;
                            p0_untitledShader_gl_FragColor = vec4(uv.xy, 0., 1.);
                        }


                        #line 10001
                        void main() {
                            // Invoke Raster Coordinate
                            in_rasterCoordinate = gl_FragCoord - vec4(ds_rasterCoordinate_offset, 0., 0.);

                            // Invoke Untitled Shader
                            p0_untitledShader_main(in_rasterCoordinate.xy);

                            sm_result = p0_untitledShader_gl_FragColor;
                        }
                    """.trimIndent()
                )
            }
        }

        context("with a ShaderToy paint shader") {
            override(shaderText) {
                /**language=glsl*/
                """
                    // This Shader's Name
                    // Other stuff.
                    
                    uniform float blueness;
                    int someGlobalVar = int(blueness * 100.);
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
                mutablePatchSet.addPatch(mainShader) {
                    link(
                        "blueness",
                        SliderFeed("Blueness", 0f, 0f, 1f, null)
                    )
                    link("iResolution", ResolutionFeed())
                    link("iTime", TimeFeed())
                    link("fragCoord", RasterCoordinateFeed())
                    stream = Stream.Main.editor()
                }
            }

            it("generates GLSL") {
                kexpect(glsl).toBe(
                    /**language=glsl*/
                    """
                        #ifdef GL_ES
                        precision mediump float;
                        #endif

                        // SparkleMotion-generated GLSL

                        layout(location = 0) out vec4 sm_result;

                        // Feed: Blueness Slider
                        uniform float in_bluenessSlider;

                        // Feed: Raster Coordinate
                        uniform vec2 ds_rasterCoordinate_offset;
                        vec4 in_rasterCoordinate;

                        // Feed: Resolution
                        uniform vec2 in_resolution;

                        // Feed: Time
                        uniform float in_time;

                        // Shader: This Shader's Name; namespace: p0
                        // This Shader's Name

                        vec4 p0_thisShaderSName_fragColor = vec4(0., 0., 0., 1.);

                        #line 5 0
                        int p0_thisShaderSName_someGlobalVar;

                        #line 6 0
                        const int p0_thisShaderSName_someConstVar = 123;

                        #line 8 0
                        int p0_thisShaderSName_anotherFunc(int i) { return i; }

                        #line 10 0
                        void p0_thisShaderSName_mainImage( out vec4 fragColor, in vec2 fragCoord ) {
                            vec2 uv = fragCoord.xy / in_resolution.xy;
                            p0_thisShaderSName_someGlobalVar = p0_thisShaderSName_anotherFunc(p0_thisShaderSName_someConstVar) + in_time * 0.;
                            fragColor = vec4(uv.xy, in_bluenessSlider, 1.);
                        }

                        void p0_thisShaderSNamei_init() {    
                        #line 5 0
                          p0_thisShaderSName_someGlobalVar = int(in_bluenessSlider * 100.);
                        }


                        #line 10001
                        void main() {
                            // Init This Shader's Name.
                            p0_thisShaderSNamei_init();

                            // Invoke Raster Coordinate
                            in_rasterCoordinate = gl_FragCoord - vec4(ds_rasterCoordinate_offset, 0., 0.);

                            // Invoke This Shader's Name
                            p0_thisShaderSName_mainImage(p0_thisShaderSName_fragColor, in_rasterCoordinate.xy);

                            sm_result = p0_thisShaderSName_fragColor;
                        }
                    """.trimIndent()
                )
            }
        }

        describe("with projection shader") {
            beforeEachTest {
                mutablePatchSet.apply {
                    addPatch(cylindricalProjection) {
                        link("pixelLocation", PixelLocationFeed())
                        link("modelInfo", ModelInfoFeed())
                        stream = Stream.Main.editor()
                    }

                    addPatch(mainShader) {
                        link("gl_FragCoord", Stream.Main.toMutable())
                        link("resolution", ResolutionFeed())
                        link("time", TimeFeed())
                        link("blueness", SliderFeed("Blueness", 0f, 0f, 1f, null))
                        stream = Stream.Main.editor()
                    }
                }
            }

            it("generates GLSL") {
                kexpect(glsl).toBe(
                    /**language=glsl*/
                    """
                        #ifdef GL_ES
                        precision mediump float;
                        #endif

                        // SparkleMotion-generated GLSL

                        layout(location = 0) out vec4 sm_result;

                        struct FixtureInfo {
                            vec3 position;
                            vec3 rotation;
                            mat4 transformation;
                            vec3 boundaryMin;
                            vec3 boundaryMax;
                        };

                        struct ModelInfo {
                            vec3 center;
                            vec3 extents;
                        };
                        
                        // Feed: Blueness Slider
                        uniform float in_bluenessSlider;

                        // Feed: Fixture Info
                        uniform FixtureInfo in_fixtureInfo;

                        // Feed: Model Info
                        uniform ModelInfo in_modelInfo;

                        // Feed: Pixel Location
                        uniform sampler2D ds_pixelLocation_texture;
                        vec3 ds_pixelLocation_getPixelCoords(vec2 rasterCoord) {
                            vec3 xyzInEntity = texelFetch(ds_pixelLocation_texture, ivec2(rasterCoord.xy), 0).xyz;
                            vec4 xyzwInModel = in_fixtureInfo.transformation * vec4(xyzInEntity, 1.);
                            return xyzwInModel.xyz;
                        }
                        vec3 in_pixelLocation;

                        // Feed: Resolution
                        uniform vec2 in_resolution;

                        // Feed: Time
                        uniform float in_time;

                        // Shader: Cylindrical Projection; namespace: p0
                        // Cylindrical Projection

                        vec2 p0_cylindricalProjectioni_result = vec2(0.);

                        #line 10 0
                        const float p0_cylindricalProjection_PI = 3.141592654;

                        #line 14 0
                        vec2 p0_cylindricalProjection_main(vec3 pixelLocation) {
                            vec3 pixelOffset = pixelLocation - in_modelInfo.center;
                            vec3 normalDelta = normalize(pixelOffset);
                            float theta = atan(abs(normalDelta.z), normalDelta.x); // theta in range [-π,π]
                            if (theta < 0.0) theta += (2.0f * p0_cylindricalProjection_PI);                 // theta in range [0,2π)
                            float u = theta / (2.0f * p0_cylindricalProjection_PI) * 2.;                    // u in range [0,1)
                            float v = (pixelOffset.y + in_modelInfo.extents.y / 2.0f) / in_modelInfo.extents.y;
                            return vec2(u, v);
                        }

                        // Shader: This Shader's Name; namespace: p1
                        // This Shader's Name

                        vec4 p1_thisShaderSName_gl_FragColor = vec4(0., 0., 0., 1.);

                        #line 7 1
                        int p1_thisShaderSName_someGlobalVar;

                        #line 8 1
                        int p1_thisShaderSName_anotherGlobalVar;

                        #line 9 1
                        const int p1_thisShaderSName_someConstVar = 123;

                        #line 11 1
                        int p1_thisShaderSName_anotherFunc(int i) { return i; }

                        #line 13 1
                        void p1_thisShaderSName_main( void ) {
                            vec2 uv = p0_cylindricalProjectioni_result.xy / in_resolution.xy;
                            p1_thisShaderSName_someGlobalVar = p1_thisShaderSName_anotherFunc(p1_thisShaderSName_someConstVar);
                            p1_thisShaderSName_gl_FragColor = vec4(uv.xy, in_bluenessSlider, 1.);
                        }

                        void p1_thisShaderSNamei_init() {    
                        #line 7 1
                          p1_thisShaderSName_someGlobalVar = int(in_bluenessSlider * 100.);
                            
                        #line 8 1
                          p1_thisShaderSName_anotherGlobalVar = p1_thisShaderSName_someGlobalVar + 1;
                        }


                        #line 10001
                        void main() {
                            // Init This Shader's Name.
                            p1_thisShaderSNamei_init();

                            // Invoke Pixel Location
                            in_pixelLocation = ds_pixelLocation_getPixelCoords(gl_FragCoord.xy);

                            // Invoke Cylindrical Projection
                            p0_cylindricalProjectioni_result = p0_cylindricalProjection_main(in_pixelLocation);

                            // Invoke This Shader's Name
                            p1_thisShaderSName_main();

                            sm_result = p1_thisShaderSName_gl_FragColor;
                        }
                    """.trimIndent()
                )
            }
        }

        describe("mixing from another stream") {
            val otherShaderActualChannel by value { "other" }

            override(shaderText) {
                """
                    // Cross-fade shader
                    uniform float fade;

                    // @return color
                    // @param inColor color
                    // @param inColor2 color
                    vec4 main(vec4 inColor, vec4 inColor2) {
                        return mix(inColor, inColor2, fade);
                    }
                """.trimIndent()
            }

            val mainPaintShader by value {
                testToolchain.import(
                    """
                        // Main Paint Shader
                        void main( void ) {
                            gl_FragColor = vec4(1., 0., 0., 1.);
                        }
                    """.trimIndent()
                )
            }

            val otherPaintShader by value {
                testToolchain.import(
                    """
                        // Other Paint Shader
                        void mainImage( out vec4 fragColor, in vec2 fragCoord ) {
                            fragColor = vec4(0., 1., 0., 1.);
                        }
                    """.trimIndent()
                )
            }

            beforeEachTest {
                mutablePatchSet.addPatch(mainPaintShader)
                mutablePatchSet.addPatch(otherPaintShader) {
                    link("fragCoord", MutableFeedPort(RasterCoordinateFeed()))
                    stream = MutableStream.from(otherShaderActualChannel)
                }

                mutablePatchSet.addPatch(mainShader) {
                    link("fade", SliderFeed("Fade", 0f, 0f, 1f, null))
                    link("inColor", MutableStream("main"))
                    link("inColor2", MutableStream("other"))
                }
            }

            it("generates GLSL") {
                kexpect(glsl).toBe(
                    /**language=glsl*/
                    """
                        #ifdef GL_ES
                        precision mediump float;
                        #endif

                        // SparkleMotion-generated GLSL

                        layout(location = 0) out vec4 sm_result;

                        // Feed: Fade Slider
                        uniform float in_fadeSlider;

                        // Feed: Raster Coordinate
                        uniform vec2 ds_rasterCoordinate_offset;
                        vec4 in_rasterCoordinate;

                        // Shader: Main Paint Shader; namespace: p0
                        // Main Paint Shader

                        vec4 p0_mainPaintShader_gl_FragColor = vec4(0., 0., 0., 1.);

                        #line 2 0
                        void p0_mainPaintShader_main( void ) {
                            p0_mainPaintShader_gl_FragColor = vec4(1., 0., 0., 1.);
                        }

                        // Shader: Other Paint Shader; namespace: p1
                        // Other Paint Shader

                        vec4 p1_otherPaintShader_fragColor = vec4(0., 0., 0., 1.);

                        #line 2 1
                        void p1_otherPaintShader_mainImage( out vec4 fragColor, in vec2 fragCoord ) {
                            fragColor = vec4(0., 1., 0., 1.);
                        }

                        // Shader: Cross-fade shader; namespace: p2
                        // Cross-fade shader

                        vec4 p2_crossFadeShaderi_result = vec4(0., 0., 0., 1.);

                        #line 7 2
                        vec4 p2_crossFadeShader_main(vec4 inColor, vec4 inColor2) {
                            return mix(inColor, inColor2, in_fadeSlider);
                        }


                        #line 10001
                        void main() {
                            // Invoke Raster Coordinate
                            in_rasterCoordinate = gl_FragCoord - vec4(ds_rasterCoordinate_offset, 0., 0.);

                            // Invoke Main Paint Shader
                            p0_mainPaintShader_main();

                            // Invoke Other Paint Shader
                            p1_otherPaintShader_mainImage(p1_otherPaintShader_fragColor, in_rasterCoordinate.xy);

                            // Invoke Cross-fade shader
                            p2_crossFadeShaderi_result = p2_crossFadeShader_main(p0_mainPaintShader_gl_FragColor, p1_otherPaintShader_fragColor);

                            sm_result = p2_crossFadeShaderi_result;
                        }
                    """.trimIndent()
                )
            }

            context("when there's no paint shader on the other stream") {
                override(otherShaderActualChannel) { "notOther" }

                it("should give a warning") {
                    expect(linkedPatch.warnings).containsExactly(
                        "No upstream shader found, using default for color.\n" +
                                "Stack:\n" +
                                "    Resolving Track[main/color] -> [Cross-fade shader].inColor2 (color)"
                    )
                }

                it("should use default for that content type") {
                    // TODO: WRONG WRONG WRONG we should tell the user that something's wrong?
                    kexpect(glsl).toBe(
                        /**language=glsl*/
                        """
                            #ifdef GL_ES
                            precision mediump float;
                            #endif

                            // SparkleMotion-generated GLSL

                            layout(location = 0) out vec4 sm_result;

                            // Feed: Fade Slider
                            uniform float in_fadeSlider;

                            // Shader: Main Paint Shader; namespace: p0
                            // Main Paint Shader

                            vec4 p0_mainPaintShader_gl_FragColor = vec4(0., 0., 0., 1.);

                            #line 2 0
                            void p0_mainPaintShader_main( void ) {
                                p0_mainPaintShader_gl_FragColor = vec4(1., 0., 0., 1.);
                            }

                            // Shader: Cross-fade shader; namespace: p1
                            // Cross-fade shader

                            vec4 p1_crossFadeShaderi_result = vec4(0., 0., 0., 1.);

                            #line 7 1
                            vec4 p1_crossFadeShader_main(vec4 inColor, vec4 inColor2) {
                                return mix(inColor, inColor2, in_fadeSlider);
                            }


                            #line 10001
                            void main() {
                                // Invoke Main Paint Shader
                                p0_mainPaintShader_main();

                                // Invoke Cross-fade shader
                                p1_crossFadeShaderi_result = p1_crossFadeShader_main(p0_mainPaintShader_gl_FragColor, vec4(0.));

                                sm_result = p1_crossFadeShaderi_result;
                            }
                        """.trimIndent()
                    )
                }
            }
        }

        context("with a shader using a struct uniform") {
            override(shaderText) {
                /**language=glsl*/
                """
                    struct FixtureInfo {
                        vec3 position;            
                        vec3 rotation; // in Euler angles
                        vec3 transformation;
                        vec3 boundaryMin;
                        vec3 boundaryMax;
                    };
                    
                    uniform FixtureInfo fixtureInfo;
                    
                    struct MovingHeadParams {
                        float pan;
                        float tilt;
                        float colorWheel;
                        float dimmer;
                    };
 
                    // @param params moving-head-params
                    void main(out MovingHeadParams params) {
                        params.pan = fixtureInfo.position.x;
                        params.tilt = fixtureInfo.position.y,
                        params.colorWheel = fixtureInfo.rotation.x,
                        params.dimmer = fixtureInfo.rotation.y;
                    }
                """.trimIndent()
            }
            override(resultContentType) { MovingHeadParams.contentType }

            beforeEachTest {
                mutablePatchSet.addPatch(mainShader) {
                    link("fixtureInfo", FixtureInfoFeed())
                }
            }

            it("generates GLSL including the struct") {
                kexpect(glsl).toBe(
                    /**language=glsl*/
                    """
                        #ifdef GL_ES
                        precision mediump float;
                        #endif

                        // SparkleMotion-generated GLSL

                        layout(location = 0) out vec4 sm_result;

                        struct MovingHeadParams {
                            float pan;
                            float tilt;
                            float colorWheel;
                            float dimmer;
                        };

                        struct FixtureInfo {
                            vec3 position;
                            vec3 rotation;
                            mat4 transformation;
                            vec3 boundaryMin;
                            vec3 boundaryMax;
                        };

                        // Feed: Fixture Info
                        uniform FixtureInfo in_fixtureInfo;

                        // Shader: Untitled Shader; namespace: p0
                        // Untitled Shader

                        MovingHeadParams p0_untitledShader_params = MovingHeadParams(0., 0., 0., 1.);

                        #line 19 0
                        void p0_untitledShader_main(out MovingHeadParams params) {
                            params.pan = in_fixtureInfo.position.x;
                            params.tilt = in_fixtureInfo.position.y,
                            params.colorWheel = in_fixtureInfo.rotation.x,
                            params.dimmer = in_fixtureInfo.rotation.y;
                        }


                        #line 10001
                        void main() {
                            // Invoke Untitled Shader
                            p0_untitledShader_main(p0_untitledShader_params);

                            sm_result = vec4(
                                p0_untitledShader_params.pan,
                                p0_untitledShader_params.tilt,
                                p0_untitledShader_params.colorWheel,
                                p0_untitledShader_params.dimmer
                            );
                        }
                    """.trimIndent()
                )
            }
        }

        context("with a shader using a struct internally") {
            override(shaderText) {
                /**language=glsl*/
                """
                    struct AnotherStruct {
                        float first;
                        float second;
                    };
                    AnotherStruct a;

                    struct MovingHeadParams {
                        float pan;
                        float tilt;
                        float colorWheel;
                        float dimmer;
                    };

                    // @return moving-head-params
                    MovingHeadParams main() {
                        AnotherStruct b;
                        return MovingHeadParams(a.first, a.second, 0., 0.);
                    }
                """.trimIndent()
            }
            override(resultContentType) { MovingHeadParams.contentType }

            beforeEachTest {
                mutablePatchSet.addPatch(mainShader) {
                    link("fixtureInfo", FixtureInfoFeed())
                }
            }

            it("generates GLSL") {
                kexpect(glsl).toBe(
                    /**language=glsl*/
                    """
                        #ifdef GL_ES
                        precision mediump float;
                        #endif

                        // SparkleMotion-generated GLSL

                        layout(location = 0) out vec4 sm_result;

                        struct MovingHeadParams {
                            float pan;
                            float tilt;
                            float colorWheel;
                            float dimmer;
                        };
                        
                        struct p0_untitledShader_AnotherStruct {
                            float first;
                            float second;
                        };

                        // Shader: Untitled Shader; namespace: p0
                        // Untitled Shader

                        MovingHeadParams p0_untitledShaderi_result = MovingHeadParams(0., 0., 0., 1.);

                        #line 5 0
                        p0_untitledShader_AnotherStruct p0_untitledShader_a;

                        #line 15 0
                        MovingHeadParams p0_untitledShader_main() {
                            p0_untitledShader_AnotherStruct b;
                            return MovingHeadParams(p0_untitledShader_a.first, p0_untitledShader_a.second, 0., 0.);
                        }


                        #line 10001
                        void main() {
                            // Invoke Untitled Shader
                            p0_untitledShaderi_result = p0_untitledShader_main();

                            sm_result = vec4(
                                p0_untitledShaderi_result.pan,
                                p0_untitledShaderi_result.tilt,
                                p0_untitledShaderi_result.colorWheel,
                                p0_untitledShaderi_result.dimmer
                            );
                        }
                    """.trimIndent()
                )
            }
        }

        context("with a shader using a struct internally, declaring a global") {
            override(shaderText) {
                /**language=glsl*/
                """
                    struct AnotherStruct {
                        float first;            
                        float second;
                    } a;
                    
                    // @return color
                    vec4 main() {
                        AnotherStruct b;
                        return vec4(a.first, a.second, 0., 0.);
                    }
                """.trimIndent()
            }
            override(resultContentType) { Color }

            beforeEachTest {
                mutablePatchSet.addPatch(mainShader) {
                    link("fixtureInfo", FixtureInfoFeed())
                }
            }

            it("generates GLSL") {
                kexpect(glsl).toBe(
                    /**language=glsl*/
                    """
                        #ifdef GL_ES
                        precision mediump float;
                        #endif

                        // SparkleMotion-generated GLSL

                        layout(location = 0) out vec4 sm_result;

                        struct p0_untitledShader_AnotherStruct {
                            float first;
                            float second;
                        };

                        // Shader: Untitled Shader; namespace: p0
                        // Untitled Shader

                        vec4 p0_untitledShaderi_result = vec4(0., 0., 0., 1.);

                        #line 1 0
                        p0_untitledShader_AnotherStruct p0_untitledShader_a;

                        #line 7 0
                        vec4 p0_untitledShader_main() {
                            p0_untitledShader_AnotherStruct b;
                            return vec4(p0_untitledShader_a.first, p0_untitledShader_a.second, 0., 0.);
                        }


                        #line 10001
                        void main() {
                            // Invoke Untitled Shader
                            p0_untitledShaderi_result = p0_untitledShader_main();

                            sm_result = p0_untitledShaderi_result;
                        }
                    """.trimIndent()
                )
            }
        }

        context("when a shader has inputs declared via abstract functions") {
            override(shaderText) {
                """
                    // Cross-fade shader
                    uniform float fade;

                    // @param uv uv-coordinate
                    // @return color
                    vec4 channelA(vec2 uv);
                    
                    // @param time time
                    // @return color
                    vec4 channelB(float time);
                    
                    uniform float time; // @type time

                    // @return color
                    // @param uvIn uv-coordinate
                    // @param inColor2 color
                    vec4 main(vec2 uvIn) {
                        return mix(
                            channelA(uvIn - fade),
                            channelB(time / 2.),
                            fade
                        );
                    }
                """.trimIndent()
            }

            val channelAShader by value {
                testToolchain.import(
                    """
                        // Channel A Shader
                        uniform float time; // @type time
                        void main(void) { gl_FragColor = vec4(1. * time, 0., gl_FragCoord.y, 1.); }
                    """.trimIndent()
                )
            }

            val channelBShader by value {
                testToolchain.import(
                    """
                        // Channel B Shader
                        uniform float time; // @type time
                        void mainImage(out vec4 fragColor, in vec2 fragCoord) {
                            fragColor = vec4(0., 1. * time, fragCoord.x, 1.);
                        }
                    """.trimIndent()
                )
            }

            beforeEachTest {
                mutablePatchSet.addPatch(testToolchain.import(
                    """
                        // Projection
                        // @return uv-coordinate
                        // @param pixelLocation xyz-coordinate
                        vec2 main(vec3 pixelLocation) {
                            return vec2(pixelLocation.xy);
                        }
                    """.trimIndent()
                )) {
                    link("pixelLocation", MutableFeedPort(RasterCoordinateFeed()))
                }

                mutablePatchSet.addPatch(mainShader) {
                    link("fade", SliderFeed("Fade", 0f, 0f, 1f, null))
                    link("channelA", MutableStream("channelA"))
                    link("channelB", MutableStream("channelB"))
                    link("time", MutableFeedPort(TimeFeed()))
                    link("uvIn", MutableStream("main"))
                }

                mutablePatchSet.addPatch(channelAShader) {
                    link("gl_FragCoord", MutableConstPort("var from downstream", GlslType.Vec4))
                    link("time", MutableFeedPort(TimeFeed()))
                    stream = MutableStream.from("channelA")
                }

                mutablePatchSet.addPatch(channelBShader) {
                    link("fragCoord", MutableFeedPort(RasterCoordinateFeed()))
                    link("time", MutableConstPort("var from downstream", GlslType.Vec4))
                    stream = MutableStream.from("channelB")
                }
            }

            it("generates GLSL, with injected data correctly resolved") {
                kexpect(glsl).toBe(
                    /**language=glsl*/
                    """
                        #ifdef GL_ES
                        precision mediump float;
                        #endif

                        // SparkleMotion-generated GLSL

                        layout(location = 0) out vec4 sm_result;

                        // Feed: Fade Slider
                        uniform float in_fadeSlider;

                        // Feed: Raster Coordinate
                        uniform vec2 ds_rasterCoordinate_offset;
                        vec4 in_rasterCoordinate;

                        // Feed: Time
                        uniform float in_time;

                        // Shader: Projection; namespace: p0
                        // Projection

                        vec2 p0_projectioni_result = vec2(0.);

                        #line 4 0
                        vec2 p0_projection_main(vec3 pixelLocation) {
                            return vec2(pixelLocation.xy);
                        }

                        // Shader: Channel A Shader; namespace: p1
                        // Channel A Shader

                        vec4 p1_channelAShader_gl_FragColor = vec4(0., 0., 0., 1.);
                        vec2 p1_global_gl_FragCoord = vec2(0.);

                        #line 3 1
                        void p1_channelAShader_main(void) { p1_channelAShader_gl_FragColor = vec4(1. * in_time, 0., p0_projectioni_result.y, 1.); }

                        // Shader: Channel B Shader; namespace: p2
                        // Channel B Shader

                        vec4 p2_channelBShader_fragColor = vec4(0., 0., 0., 1.);
                        float p2_global_time = 0.;

                        #line 3 2
                        void p2_channelBShader_mainImage(out vec4 fragColor, in vec2 fragCoord) {
                            fragColor = vec4(0., 1. * p2_global_time, fragCoord.x, 1.);
                        }

                        // Shader: Cross-fade shader; namespace: p3
                        // Cross-fade shader

                        vec4 p3_crossFadeShaderi_result = vec4(0., 0., 0., 1.);

                        #line 6 3
                        vec4 p3_crossFadeShader_channelA(vec2 uv) {
                            // Invoke Channel A Shader
                            p1_global_gl_FragCoord = uv;
                            p1_channelAShader_main();

                            return p1_channelAShader_gl_FragColor;
                        }

                        #line 10 3
                        vec4 p3_crossFadeShader_channelB(float time) {
                            // Invoke Channel B Shader
                            p2_global_time = time;
                            p2_channelBShader_mainImage(p2_channelBShader_fragColor, in_rasterCoordinate.xy);

                            return p2_channelBShader_fragColor;
                        }

                        #line 17 3
                        vec4 p3_crossFadeShader_main(vec2 uvIn) {
                            return mix(
                                p3_crossFadeShader_channelA(uvIn - in_fadeSlider),
                                p3_crossFadeShader_channelB(in_time / 2.),
                                in_fadeSlider
                            );
                        }


                        #line 10001
                        void main() {
                            // Invoke Raster Coordinate
                            in_rasterCoordinate = gl_FragCoord - vec4(ds_rasterCoordinate_offset, 0., 0.);

                            // Invoke Projection
                            p0_projectioni_result = p0_projection_main(in_rasterCoordinate);

                            // Invoke Cross-fade shader
                            p3_crossFadeShaderi_result = p3_crossFadeShader_main(p0_projectioni_result);

                            sm_result = p3_crossFadeShaderi_result;
                        }
                    """.trimIndent()
                )
            }
        }

        context("when a shader sets a global variable with the same name as a struct member") {
            override(shaderText) {
                """
                    struct BeatInfo {
                        float beat;
                    };
                    uniform BeatInfo beatInfo;

                    float beat = beatInfo.beat;

                    // @return color
                    vec4 main() {
                        float uhh = 1. + beat; 
                        return vec4(beat, uh, 0., 1.);
                    }
                """.trimIndent()
            }

            beforeEachTest {
                mutablePatchSet.addPatch(mainShader)
            }

            it("doesn't perform namespacing on struct member names") {
                kexpect(glsl).toBe(
                    /**language=glsl*/
                    """
                        #ifdef GL_ES
                        precision mediump float;
                        #endif

                        // SparkleMotion-generated GLSL

                        layout(location = 0) out vec4 sm_result;

                        // Shader: Untitled Shader; namespace: p0
                        // Untitled Shader

                        vec4 p0_untitledShaderi_result = vec4(0., 0., 0., 1.);

                        #line 6 0
                        float p0_untitledShader_beat;

                        #line 9 0
                        vec4 p0_untitledShader_main() {
                            float uhh = 1. + p0_untitledShader_beat; 
                            return vec4(p0_untitledShader_beat, uh, 0., 1.);
                        }

                        void p0_untitledShaderi_init() {    
                        #line 6 0
                          p0_untitledShader_beat = p0_untitledShader_beatInfo.beat;
                        }


                        #line 10001
                        void main() {
                            // Init Untitled Shader.
                            p0_untitledShaderi_init();

                            // Invoke Untitled Shader
                            p0_untitledShaderi_result = p0_untitledShader_main();

                            sm_result = p0_untitledShaderi_result;
                        }
                    """.trimIndent()
                )
            }
        }
    }
})

