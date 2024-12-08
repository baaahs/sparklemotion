package baaahs.gl.patch

import baaahs.TestModel
import baaahs.app.ui.editor.PortLinkOption
import baaahs.control.OpenButtonControl
import baaahs.device.PixelArrayDevice
import baaahs.getBang
import baaahs.gl.autoWire
import baaahs.gl.patch.ContentType.Companion.Color
import baaahs.gl.render.RenderManager
import baaahs.gl.testToolchain
import baaahs.glsl.Shaders
import baaahs.kotest.value
import baaahs.only
import baaahs.plugin.core.feed.TimeFeed
import baaahs.shaders.fakeFixture
import baaahs.show.Feed
import baaahs.show.Panel
import baaahs.show.Shader
import baaahs.show.Stream
import baaahs.show.live.ActivePatchSet
import baaahs.show.live.LinkedPatch
import baaahs.show.live.ShowOpener
import baaahs.show.mutable.*
import baaahs.shows.FakeGlContext
import baaahs.shows.FakeShowPlayer
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*
import io.kotest.matchers.collections.shouldContainExactly

@Suppress("unused")
class PatchResolverSpec : DescribeSpec({
    fun autoWire(
        vararg shaders: Shader,
        stream: Stream = Stream.Main,
        block: (UnresolvedPatches.() -> Unit)? = null
    ): MutablePatchSet {
        return testToolchain.autoWire(*shaders, stream = stream)
            .apply { block?.invoke(this) }
            .acceptSuggestedLinkOptions().confirm()
    }

    describe("Layering of patch links") {
        val uvShader = Shaders.cylindricalProjection
        val blackShader by value {
            Shader(
                "Black Shader", "void main() {\n  gl_FragColor = vec4(0.);\n}"
            )
        }
        val orangeShader by value {
            Shader(
                "Orange Shader",
                /**language=glsl*/
                """
                    uniform float time;
                    void main() {
                      gl_FragColor = vec4(1., .5, time, gl_FragCoord.x);
                    }
                """.trimIndent()
            )
        }
        val brightnessFilter by value {
            Shader(
                "Brightness Filter",
                /**language=glsl*/
                """
                    uniform float brightness; // @@Slider min=0 max=1 default=1
                    // @return color
                    // @param colorIn color
                    vec4 main(vec4 colorIn) {
                      return colorIn * brightness;
                    }
                """.trimIndent()
            )
        }
        val wobblyTimeFilter by value {
            Shader(
                "Wobbly Time Filter",
                /**language=glsl*/
                src = """
                    uniform float time; // @type time
                    // @return time
                    float main() { return time + sin(time); }
                """.trimIndent()
            )
        }
        val mainPanel = MutablePanel(Panel("Main"))
        val mutableShow by value {
            MutableShow("test show") {
                editLayouts {
                    panels["main"] = mainPanel
                    editLayout("default") {
                        tabs.add(MutableLegacyTab("Tab"))
                    }
                }
            }
        }
        val show by value {
            val show = mutableShow.build(ShowBuilder())
            ShowOpener(testToolchain, show, FakeShowPlayer()).openShow()
        }
        val linkedPatch by value { generateLinkedProgram(show.allFeeds, show.buildActivePatchSet()) }

        fun clickButton(id: String) {
            (show.allControls.associateBy { it.id }.getBang(id, "control") as OpenButtonControl)
                .click()
        }

        context("for a show with a couple buttons") {
            beforeEach {
                mutableShow.apply {
                    addPatch(autoWire(uvShader, blackShader))

                    addButton(mainPanel, "Brightness") {
                        addPatch(autoWire(brightnessFilter))
                    }

                    addButton(mainPanel, "Orange") {
                        addPatch(autoWire(orangeShader).apply {
                            mutablePatches.first().incomingLinks.forEach { (k, v) ->
                                println("$k = $v")
                            }
                        })
                    }
                }
            }

            it("merges layered patches into a single patch") {
                clickButton("brightness")
                clickButton("orange")

                linkedPatch.toGlsl().shouldBe(
                    /**language=glsl*/
                    """
                        #ifdef GL_ES
                        precision highp float;
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

                        // Feed: Brightness Slider
                        uniform float in_brightnessSlider;

                        // Feed: Fixture Info
                        uniform FixtureInfo in_fixtureInfo;

                        // Feed: Model Info
                        uniform ModelInfo in_modelInfo;

                        // Feed: orangeShader-patch gl_FragCoord offset
                        uniform vec2 in_orangeShaderPatchGlFragCoordOffset;

                        // Feed: orangeShader-patch gl_FragCoord scale Slider
                        uniform float in_orangeShaderPatchGlFragCoordScaleSlider;

                        // Feed: Pixel Location
                        uniform sampler2D ds_pixelLocation_texture;
                        vec3 ds_pixelLocation_getPixelCoords(vec2 rasterCoord) {
                            vec3 xyzInEntity = texelFetch(ds_pixelLocation_texture, ivec2(rasterCoord.xy), 0).xyz;
                            vec4 xyzwInModel = in_fixtureInfo.transformation * vec4(xyzInEntity, 1.);
                            return xyzwInModel.xyz;
                        }
                        vec3 in_pixelLocation;

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

                        // Shader: Position and Scale patchmod for Orange Shader; namespace: p1m0
                        // Position and Scale patchmod for Orange Shader

                        vec2 p1m0_positionAndScalePatchmodForOrangeShaderi_result = vec2(0.);

                        #line 5 1
                        vec2 p1m0_positionAndScalePatchmodForOrangeShader_main(vec2 uvIn, vec2 offset, float scale) {
                            return (uvIn - .5) / scale - -offset + .5;
                        }

                        // Shader: Orange Shader; namespace: p1
                        // Orange Shader

                        vec4 p1_orangeShader_gl_FragColor = vec4(0., 0., 0., 1.);

                        #line 2 1
                        void p1_orangeShader_main() {
                          p1_orangeShader_gl_FragColor = vec4(1., .5, in_time, p1m0_positionAndScalePatchmodForOrangeShaderi_result.x);
                        }

                        // Shader: Brightness Filter; namespace: p2
                        // Brightness Filter

                        vec4 p2_brightnessFilteri_result = vec4(0., 0., 0., 1.);

                        #line 4 2
                        vec4 p2_brightnessFilter_main(vec4 colorIn) {
                          return colorIn * in_brightnessSlider;
                        }


                        #line 10001
                        void main() {
                            // Invoke Pixel Location
                            in_pixelLocation = ds_pixelLocation_getPixelCoords(gl_FragCoord.xy);

                            // Invoke Cylindrical Projection
                            p0_cylindricalProjectioni_result = p0_cylindricalProjection_main(in_pixelLocation);

                            // Invoke Position and Scale patchmod for Orange Shader
                            p1m0_positionAndScalePatchmodForOrangeShaderi_result = p1m0_positionAndScalePatchmodForOrangeShader_main(p0_cylindricalProjectioni_result, in_orangeShaderPatchGlFragCoordOffset, in_orangeShaderPatchGlFragCoordScaleSlider);

                            // Invoke Orange Shader
                            p1_orangeShader_main();

                            // Invoke Brightness Filter
                            p2_brightnessFilteri_result = p2_brightnessFilter_main(p1_orangeShader_gl_FragColor);

                            sm_result = p2_brightnessFilteri_result;
                        }
                        
                    """.trimIndent()
                )
            }

            context("with a feed filter") {
                beforeEach {
                    mutableShow.apply {
                        addButton(mainPanel, "Time Wobble") {
                            addPatch(autoWire(wobblyTimeFilter, stream = Stream("time")).apply {
                                mutablePatches.only("patch")
                                    .incomingLinks["time"] = MutableFeedPort(TimeFeed())
                            })
                        }
                    }
                }

                it("merges layered patches into a single patch") {
                    clickButton("brightness")
                    clickButton("orange")
                    clickButton("timeWobble")

                    linkedPatch.toGlsl().shouldBe(
                        /**language=glsl*/
                        """
                            #ifdef GL_ES
                            precision highp float;
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

                            // Feed: Brightness Slider
                            uniform float in_brightnessSlider;

                            // Feed: Fixture Info
                            uniform FixtureInfo in_fixtureInfo;

                            // Feed: Model Info
                            uniform ModelInfo in_modelInfo;

                            // Feed: orangeShader-patch gl_FragCoord offset
                            uniform vec2 in_orangeShaderPatchGlFragCoordOffset;

                            // Feed: orangeShader-patch gl_FragCoord scale Slider
                            uniform float in_orangeShaderPatchGlFragCoordScaleSlider;

                            // Feed: Pixel Location
                            uniform sampler2D ds_pixelLocation_texture;
                            vec3 ds_pixelLocation_getPixelCoords(vec2 rasterCoord) {
                                vec3 xyzInEntity = texelFetch(ds_pixelLocation_texture, ivec2(rasterCoord.xy), 0).xyz;
                                vec4 xyzwInModel = in_fixtureInfo.transformation * vec4(xyzInEntity, 1.);
                                return xyzwInModel.xyz;
                            }
                            vec3 in_pixelLocation;

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

                            // Shader: Wobbly Time Filter; namespace: p1
                            // Wobbly Time Filter

                            float p1_wobblyTimeFilteri_result = 0.;

                            #line 3 1
                            float p1_wobblyTimeFilter_main() { return in_time + sin(in_time); }

                            // Shader: Position and Scale patchmod for Orange Shader; namespace: p2m0
                            // Position and Scale patchmod for Orange Shader

                            vec2 p2m0_positionAndScalePatchmodForOrangeShaderi_result = vec2(0.);

                            #line 5 2
                            vec2 p2m0_positionAndScalePatchmodForOrangeShader_main(vec2 uvIn, vec2 offset, float scale) {
                                return (uvIn - .5) / scale - -offset + .5;
                            }

                            // Shader: Orange Shader; namespace: p2
                            // Orange Shader

                            vec4 p2_orangeShader_gl_FragColor = vec4(0., 0., 0., 1.);

                            #line 2 2
                            void p2_orangeShader_main() {
                              p2_orangeShader_gl_FragColor = vec4(1., .5, p1_wobblyTimeFilteri_result, p2m0_positionAndScalePatchmodForOrangeShaderi_result.x);
                            }

                            // Shader: Brightness Filter; namespace: p3
                            // Brightness Filter

                            vec4 p3_brightnessFilteri_result = vec4(0., 0., 0., 1.);

                            #line 4 3
                            vec4 p3_brightnessFilter_main(vec4 colorIn) {
                              return colorIn * in_brightnessSlider;
                            }


                            #line 10001
                            void main() {
                                // Invoke Pixel Location
                                in_pixelLocation = ds_pixelLocation_getPixelCoords(gl_FragCoord.xy);

                                // Invoke Cylindrical Projection
                                p0_cylindricalProjectioni_result = p0_cylindricalProjection_main(in_pixelLocation);

                                // Invoke Wobbly Time Filter
                                p1_wobblyTimeFilteri_result = p1_wobblyTimeFilter_main();

                                // Invoke Position and Scale patchmod for Orange Shader
                                p2m0_positionAndScalePatchmodForOrangeShaderi_result = p2m0_positionAndScalePatchmodForOrangeShader_main(p0_cylindricalProjectioni_result, in_orangeShaderPatchGlFragCoordOffset, in_orangeShaderPatchGlFragCoordScaleSlider);

                                // Invoke Orange Shader
                                p2_orangeShader_main();

                                // Invoke Brightness Filter
                                p3_brightnessFilteri_result = p3_brightnessFilter_main(p2_orangeShader_gl_FragColor);

                                sm_result = p3_brightnessFilteri_result;
                            }
                        
                        """.trimIndent()
                    )
                }
            }
        }

        context("with a color mixer") {
            beforeEach {
                mutableShow.apply {
                    addPatch(autoWire(uvShader, blackShader))
                    addPatch(
                        autoWire(
                            Shader(
                                "A Main Shader",
                                /**language=glsl*/
                                """
                                    uniform float time;
                                    void main() {
                                      gl_FragColor = vec4(time, time, time, gl_FragCoord.x);
                                    }
                                """.trimIndent()
                            ), stream = Stream("main")
                        )
                    )
                    addPatch(
                        testToolchain.autoWire(
                            Shader(
                                "Fade",
                                /**language=glsl*/
                                """
                                    uniform float fade;
                                    varying vec4 otherColorStream; // @type color
                
                                    // @return color
                                    // @param colorIn color
                                    vec4 main(vec4 colorIn) {
                                        return mix(colorIn, otherColorStream, fade);
                                    }
                                """.trimIndent()
                            )
                        ).apply {
                            linkOptionsFor("otherColorStream").apply {
                                clear()
                                add(PortLinkOption(MutableStream("other")))
                            }
                        }.acceptSuggestedLinkOptions().confirm()
                    )
                    addPatch(autoWire(orangeShader, stream = Stream("other")))
                    addPatch(autoWire(wobblyTimeFilter, stream = Stream("time")))
                }
            }

            it("merges layered patches into a single patch") {
                linkedPatch.toGlsl().shouldBe(
                    /**language=glsl*/
                    """
                        #ifdef GL_ES
                        precision highp float;
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

                        // Feed: aMainShader-patch gl_FragCoord offset
                        uniform vec2 in_aMainShaderPatchGlFragCoordOffset;

                        // Feed: aMainShader-patch gl_FragCoord scale Slider
                        uniform float in_aMainShaderPatchGlFragCoordScaleSlider;

                        // Feed: Fade Slider
                        uniform float in_fadeSlider;

                        // Feed: Fixture Info
                        uniform FixtureInfo in_fixtureInfo;

                        // Feed: Model Info
                        uniform ModelInfo in_modelInfo;

                        // Feed: orangeShader-patch gl_FragCoord offset
                        uniform vec2 in_orangeShaderPatchGlFragCoordOffset;

                        // Feed: orangeShader-patch gl_FragCoord scale Slider
                        uniform float in_orangeShaderPatchGlFragCoordScaleSlider;

                        // Feed: Pixel Location
                        uniform sampler2D ds_pixelLocation_texture;
                        vec3 ds_pixelLocation_getPixelCoords(vec2 rasterCoord) {
                            vec3 xyzInEntity = texelFetch(ds_pixelLocation_texture, ivec2(rasterCoord.xy), 0).xyz;
                            vec4 xyzwInModel = in_fixtureInfo.transformation * vec4(xyzInEntity, 1.);
                            return xyzwInModel.xyz;
                        }
                        vec3 in_pixelLocation;

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

                        // Shader: Wobbly Time Filter; namespace: p1
                        // Wobbly Time Filter

                        float p1_wobblyTimeFilteri_result = 0.;

                        #line 3 1
                        float p1_wobblyTimeFilter_main() { return in_time + sin(in_time); }

                        // Shader: Position and Scale patchmod for A Main Shader; namespace: p2m0
                        // Position and Scale patchmod for A Main Shader

                        vec2 p2m0_positionAndScalePatchmodForAMainShaderi_result = vec2(0.);

                        #line 5 2
                        vec2 p2m0_positionAndScalePatchmodForAMainShader_main(vec2 uvIn, vec2 offset, float scale) {
                            return (uvIn - .5) / scale - -offset + .5;
                        }

                        // Shader: A Main Shader; namespace: p2
                        // A Main Shader

                        vec4 p2_aMainShader_gl_FragColor = vec4(0., 0., 0., 1.);

                        #line 2 2
                        void p2_aMainShader_main() {
                          p2_aMainShader_gl_FragColor = vec4(p1_wobblyTimeFilteri_result, p1_wobblyTimeFilteri_result, p1_wobblyTimeFilteri_result, p2m0_positionAndScalePatchmodForAMainShaderi_result.x);
                        }

                        // Shader: Position and Scale patchmod for Orange Shader; namespace: p3m0
                        // Position and Scale patchmod for Orange Shader

                        vec2 p3m0_positionAndScalePatchmodForOrangeShaderi_result = vec2(0.);

                        #line 5 3
                        vec2 p3m0_positionAndScalePatchmodForOrangeShader_main(vec2 uvIn, vec2 offset, float scale) {
                            return (uvIn - .5) / scale - -offset + .5;
                        }

                        // Shader: Orange Shader; namespace: p3
                        // Orange Shader

                        vec4 p3_orangeShader_gl_FragColor = vec4(0., 0., 0., 1.);

                        #line 2 3
                        void p3_orangeShader_main() {
                          p3_orangeShader_gl_FragColor = vec4(1., .5, p1_wobblyTimeFilteri_result, p3m0_positionAndScalePatchmodForOrangeShaderi_result.x);
                        }

                        // Shader: Fade; namespace: p4
                        // Fade

                        vec4 p4_fadei_result = vec4(0., 0., 0., 1.);

                        #line 6 4
                        vec4 p4_fade_main(vec4 colorIn) {
                            return mix(colorIn, p3_orangeShader_gl_FragColor, in_fadeSlider);
                        }


                        #line 10001
                        void main() {
                            // Invoke Pixel Location
                            in_pixelLocation = ds_pixelLocation_getPixelCoords(gl_FragCoord.xy);

                            // Invoke Cylindrical Projection
                            p0_cylindricalProjectioni_result = p0_cylindricalProjection_main(in_pixelLocation);

                            // Invoke Wobbly Time Filter
                            p1_wobblyTimeFilteri_result = p1_wobblyTimeFilter_main();

                            // Invoke Position and Scale patchmod for A Main Shader
                            p2m0_positionAndScalePatchmodForAMainShaderi_result = p2m0_positionAndScalePatchmodForAMainShader_main(p0_cylindricalProjectioni_result, in_aMainShaderPatchGlFragCoordOffset, in_aMainShaderPatchGlFragCoordScaleSlider);

                            // Invoke A Main Shader
                            p2_aMainShader_main();

                            // Invoke Position and Scale patchmod for Orange Shader
                            p3m0_positionAndScalePatchmodForOrangeShaderi_result = p3m0_positionAndScalePatchmodForOrangeShader_main(p0_cylindricalProjectioni_result, in_orangeShaderPatchGlFragCoordOffset, in_orangeShaderPatchGlFragCoordScaleSlider);

                            // Invoke Orange Shader
                            p3_orangeShader_main();

                            // Invoke Fade
                            p4_fadei_result = p4_fade_main(p2_aMainShader_gl_FragColor);

                            sm_result = p4_fadei_result;
                        }

                    """.trimIndent()
                )
            }
        }
    }

    describe("when a node is reachable by multiple paths") {
        val pinksShader by value {
            testToolchain.import(
                """
                    // Pinks
                    uniform vec2 resolution;
                    uniform float redness;
                    void main(void) {
                        gl_FragColor = vec4(redness, gl_FragCoord.xy / resolution, 1.0);
                    }
                """.trimIndent()
            )
        }

        val projectionShader by value {
            testToolchain.import(
                """
                    // UV Projection
                    struct ModelInfo {
                        vec3 center;
                        vec3 extents;
                    };
                    uniform ModelInfo modelInfo;

                    // @return uv-coordinate
                    // @param pixelLocation xyz-coordinate
                    vec2 main(vec3 pixelLocation) {
                        vec3 pixelOffset = (pixelLocation - modelInfo.center) / modelInfo.extents + .5;
                        return vec2(pixelOffset.x, pixelOffset.y);
                    }

                """.trimIndent()
            )
        }

        val rippleShader by value {
            testToolchain.import(
                """
                    // Ripple
                    uniform float time;
                    uniform float rippleAmount; // @type float

                    // @return uv-coordinate
                    // @param uvIn uv-coordinate
                    vec2 main(vec2 uvIn) {
                        vec2 p = -1.0 + 2.0 * uvIn;
                        float len = length(p);
                        return uvIn + (p/len)*sin(len*12.0-time*4.0)*0.1 * rippleAmount;
                    }
                """.trimIndent()
            )
        }

        val scaleShader by value {
            testToolchain.import(
                """
                    // Scale
                    uniform float size; // @@Slider min=0.25 max=4 default=1

                    // @return uv-coordinate
                    // @param uvIn uv-coordinate
                    vec2 main(vec2 uvIn) {
                      return (uvIn - .5) / size + .5;
                    }
                """.trimIndent()
            )
        }

        val hsvShader by value {
            testToolchain.import(
                """
                    // HSV
                    uniform float hue; // @@Slider min=0 max=1.25 default=1
                    uniform float saturation; // @@Slider min=0 max=1.25 default=1
                    uniform float brightness; // @@Slider min=0 max=1.25 default=1

                    vec3 rgb2hsv(vec3 c) { return vec3(c); }
                    vec3 hsv2rgb(vec3 c) { return vec3(c); }

                    // @return color
                    // @param inColor color
                    vec4 main(vec4 inColor) {
                        if (saturation == 1.) return inColor;

                        vec4 clampedColor = clamp(inColor, 0., 1.);
                        vec3 hsv = rgb2hsv(clampedColor.rgb);
                        hsv.x += hue;
                        hsv.y *= saturation;
                        hsv.z *= brightness;
                        return vec4(hsv2rgb(hsv), clampedColor.a);
                    }
                """.trimIndent()
            )
        }

        val badgerShader by value {
            testToolchain.import(
                """
                    // Badger Overlay

                    // @@Image
                    // @param uv uv-coordinate
                    // @return color
                    vec4 image(vec2 uv);

                    // @param uv uv-coordinate
                    // @return color
                    vec4 upstreamImage(vec2 uv);

                    // @return color
                    vec4 main() {
                        vec4 c = image(gl_FragCoord);
                        return mix(upstreamImage(gl_FragCoord), c, c.a);
                    }
                """.trimIndent()
            )
        }

        val show by value {
            MutableShow("show") {
                addPatch(
                    autoWire(
                        pinksShader,
                        badgerShader,
                        projectionShader,
                        rippleShader,
                        scaleShader,
                        hsvShader
                    ) {
                        editShader(scaleShader) { priority = 10f }
                        editShader(hsvShader) { priority = 10f }
                    }
                )
            }.build(ShowBuilder())
                .let {
                    ShowOpener(testToolchain, it, FakeShowPlayer()).openShow()
                }
        }

        val linkedProgram by value {
            generateLinkedProgram(show.allFeeds, show.buildActivePatchSet())
        }

        it("correctly finds the max reference depth of that node") {
            val linkNodes = linkedProgram.linkNodes
                .filter { (p,l) -> p is LinkedPatch }
                .values
                .sortedBy { it.index }
                .map { it.toString() }

            linkNodes.shouldContainExactly(
                "LinkNode(UV Projection, id='uvProjection', maxObservedDepth=7, index=0)",
                "LinkNode(Ripple, id='ripple', maxObservedDepth=5, index=1)",
                "LinkNode(Position and Scale patchmod for Ripple, id='positionAndScalePatchmodForRipple', maxObservedDepth=6, index=1, modIndex=0)",
                "LinkNode(Scale, id='scale', maxObservedDepth=3, index=2)",
                "LinkNode(Position and Scale patchmod for Scale, id='positionAndScalePatchmodForScale', maxObservedDepth=4, index=2, modIndex=0)",
                "LinkNode(Pinks, id='pinks', maxObservedDepth=2, index=3)",
                "LinkNode(Badger Overlay, id='badgerOverlay', maxObservedDepth=1, index=4)",
                "LinkNode(Position and Scale patchmod for Badger Overlay, id='positionAndScalePatchmodForBadgerOverlay', maxObservedDepth=2, index=4, modIndex=0)",
                "LinkNode(HSV, id='hsv', maxObservedDepth=0, index=5)",
            )
        }

        it("generates GLSL with nodes called in the right order") {
            linkedProgram.toGlsl().shouldBe(
                /**language=glsl*/
                """
                    #ifdef GL_ES
                    precision highp float;
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

                    // Feed: badgerOverlay-patch gl_FragCoord offset
                    uniform vec2 in_badgerOverlayPatchGlFragCoordOffset;

                    // Feed: badgerOverlay-patch gl_FragCoord scale Slider
                    uniform float in_badgerOverlayPatchGlFragCoordScaleSlider;

                    // Feed: Brightness Slider
                    uniform float in_brightnessSlider;

                    // Feed: Fixture Info
                    uniform FixtureInfo in_fixtureInfo;

                    // Feed: Hue Slider
                    uniform float in_hueSlider;

                    // Feed: Image Image
                    uniform sampler2D ds_in_imageImage_texture;

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

                    // Feed: Redness Slider
                    uniform float in_rednessSlider;

                    // Feed: Resolution
                    uniform vec2 in_resolution;

                    // Feed: Ripple Amount Slider
                    uniform float in_rippleAmountSlider;

                    // Feed: ripple-patch uvIn offset
                    uniform vec2 in_ripplePatchUvInOffset;

                    // Feed: ripple-patch uvIn scale Slider
                    uniform float in_ripplePatchUvInScaleSlider;

                    // Feed: Saturation Slider
                    uniform float in_saturationSlider;

                    // Feed: scale-patch uvIn offset
                    uniform vec2 in_scalePatchUvInOffset;

                    // Feed: scale-patch uvIn scale Slider
                    uniform float in_scalePatchUvInScaleSlider;

                    // Feed: Size Slider
                    uniform float in_sizeSlider;

                    // Feed: Time
                    uniform float in_time;

                    // Shader: UV Projection; namespace: p0
                    // UV Projection

                    vec2 p0_uvProjectioni_result = vec2(0.);

                    #line 10 0
                    vec2 p0_uvProjection_main(vec3 pixelLocation) {
                        vec3 pixelOffset = (pixelLocation - in_modelInfo.center) / in_modelInfo.extents + .5;
                        return vec2(pixelOffset.x, pixelOffset.y);
                    }

                    // Shader: Position and Scale patchmod for Ripple; namespace: p1m0
                    // Position and Scale patchmod for Ripple

                    vec2 p1m0_positionAndScalePatchmodForRipplei_result = vec2(0.);

                    #line 5 1
                    vec2 p1m0_positionAndScalePatchmodForRipple_main(vec2 uvIn, vec2 offset, float scale) {
                        return (uvIn - .5) / scale - -offset + .5;
                    }

                    // Shader: Ripple; namespace: p1
                    // Ripple

                    vec2 p1_ripplei_result = vec2(0.);

                    #line 7 1
                    vec2 p1_ripple_main(vec2 uvIn) {
                        vec2 p = -1.0 + 2.0 * uvIn;
                        float len = length(p);
                        return uvIn + (p/len)*sin(len*12.0-in_time*4.0)*0.1 * in_rippleAmountSlider;
                    }

                    // Shader: Position and Scale patchmod for Scale; namespace: p2m0
                    // Position and Scale patchmod for Scale

                    vec2 p2m0_positionAndScalePatchmodForScalei_result = vec2(0.);

                    #line 5 2
                    vec2 p2m0_positionAndScalePatchmodForScale_main(vec2 uvIn, vec2 offset, float scale) {
                        return (uvIn - .5) / scale - -offset + .5;
                    }

                    // Shader: Scale; namespace: p2
                    // Scale

                    vec2 p2_scalei_result = vec2(0.);

                    #line 6 2
                    vec2 p2_scale_main(vec2 uvIn) {
                      return (uvIn - .5) / in_sizeSlider + .5;
                    }

                    // Shader: Pinks; namespace: p3
                    // Pinks

                    vec4 p3_pinks_gl_FragColor = vec4(0., 0., 0., 1.);
                    vec2 p3_global_gl_FragCoord = vec2(0.);

                    #line 4 3
                    void p3_pinks_main(void) {
                        p3_pinks_gl_FragColor = vec4(in_rednessSlider, p2_scalei_result.xy / in_resolution, 1.0);
                    }

                    // Shader: Position and Scale patchmod for Badger Overlay; namespace: p4m0
                    // Position and Scale patchmod for Badger Overlay

                    vec2 p4m0_positionAndScalePatchmodForBadgerOverlayi_result = vec2(0.);

                    #line 5 4
                    vec2 p4m0_positionAndScalePatchmodForBadgerOverlay_main(vec2 uvIn, vec2 offset, float scale) {
                        return (uvIn - .5) / scale - -offset + .5;
                    }

                    // Shader: Badger Overlay; namespace: p4
                    // Badger Overlay

                    vec4 p4_badgerOverlayi_result = vec4(0., 0., 0., 1.);

                    #line 6 4
                    vec4 p4_badgerOverlay_image(vec2 uv) {
                        return texture(ds_in_imageImage_texture, vec2(uv.x, 1. - uv.y));
                    }

                    #line 10 4
                    vec4 p4_badgerOverlay_upstreamImage(vec2 uv) {
                        // Invoke Pinks
                        p3_global_gl_FragCoord = uv;
                        p3_pinks_main();

                        return p3_pinks_gl_FragColor;
                    }

                    #line 13 4
                    vec4 p4_badgerOverlay_main() {
                        vec4 c = p4_badgerOverlay_image(p4m0_positionAndScalePatchmodForBadgerOverlayi_result);
                        return mix(p4_badgerOverlay_upstreamImage(p4m0_positionAndScalePatchmodForBadgerOverlayi_result), c, c.a);
                    }

                    // Shader: HSV; namespace: p5
                    // HSV

                    vec4 p5_hsvi_result = vec4(0., 0., 0., 1.);

                    #line 6 5
                    vec3 p5_hsv_rgb2hsv(vec3 c) { return vec3(c); }

                    #line 7 5
                    vec3 p5_hsv_hsv2rgb(vec3 c) { return vec3(c); }

                    #line 11 5
                    vec4 p5_hsv_main(vec4 inColor) {
                        if (in_saturationSlider == 1.) return inColor;

                        vec4 clampedColor = clamp(inColor, 0., 1.);
                        vec3 hsv = p5_hsv_rgb2hsv(clampedColor.rgb);
                        hsv.x += in_hueSlider;
                        hsv.y *= in_saturationSlider;
                        hsv.z *= in_brightnessSlider;
                        return vec4(p5_hsv_hsv2rgb(hsv), clampedColor.a);
                    }


                    #line 10001
                    void main() {
                        // Invoke Pixel Location
                        in_pixelLocation = ds_pixelLocation_getPixelCoords(gl_FragCoord.xy);

                        // Invoke UV Projection
                        p0_uvProjectioni_result = p0_uvProjection_main(in_pixelLocation);

                        // Invoke Position and Scale patchmod for Ripple
                        p1m0_positionAndScalePatchmodForRipplei_result = p1m0_positionAndScalePatchmodForRipple_main(p0_uvProjectioni_result, in_ripplePatchUvInOffset, in_ripplePatchUvInScaleSlider);

                        // Invoke Ripple
                        p1_ripplei_result = p1_ripple_main(p1m0_positionAndScalePatchmodForRipplei_result);

                        // Invoke Position and Scale patchmod for Scale
                        p2m0_positionAndScalePatchmodForScalei_result = p2m0_positionAndScalePatchmodForScale_main(p1_ripplei_result, in_scalePatchUvInOffset, in_scalePatchUvInScaleSlider);

                        // Invoke Scale
                        p2_scalei_result = p2_scale_main(p2m0_positionAndScalePatchmodForScalei_result);

                        // Invoke Position and Scale patchmod for Badger Overlay
                        p4m0_positionAndScalePatchmodForBadgerOverlayi_result = p4m0_positionAndScalePatchmodForBadgerOverlay_main(p2_scalei_result, in_badgerOverlayPatchGlFragCoordOffset, in_badgerOverlayPatchGlFragCoordScaleSlider);

                        // Invoke Badger Overlay
                        p4_badgerOverlayi_result = p4_badgerOverlay_main();

                        // Invoke HSV
                        p5_hsvi_result = p5_hsv_main(p4_badgerOverlayi_result);

                        sm_result = p5_hsvi_result;
                    }

                """.trimIndent()
            )
        }
    }
})

private fun generateLinkedProgram(feeds: Map<String, Feed>, activePatchSet: ActivePatchSet): LinkedProgram {
    val model = TestModel
    val renderManager = RenderManager(FakeGlContext())
    val fixture = model.allEntities.first()
    val renderTarget = renderManager.addFixture(fakeFixture(1, fixture, model = model))
    val patchResolution = ProgramResolver(listOf(renderTarget), activePatchSet, renderManager)
    val portDiagram = patchResolution.portDiagrams
        .getBang(PixelArrayDevice, "fixture type")
        .only("port diagram to render targets")
        .first
    return portDiagram.resolvePatch(Stream.Main, Color, feeds)!!
}
