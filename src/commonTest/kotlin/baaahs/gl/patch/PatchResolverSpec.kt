package baaahs.gl.patch

import baaahs.TestModel
import baaahs.app.ui.editor.PortLinkOption
import baaahs.control.OpenButtonControl
import baaahs.device.PixelArrayDevice
import baaahs.getBang
import baaahs.gl.autoWire
import baaahs.gl.kexpect
import baaahs.gl.patch.ContentType.Companion.Color
import baaahs.gl.render.RenderManager
import baaahs.gl.testToolchain
import baaahs.glsl.Shaders
import baaahs.only
import baaahs.plugin.core.datasource.TimeDataSource
import baaahs.shaders.fakeFixture
import baaahs.show.DataSource
import baaahs.show.Panel
import baaahs.show.Shader
import baaahs.show.ShaderChannel
import baaahs.show.live.ActivePatchSet
import baaahs.show.live.ShowOpener
import baaahs.show.mutable.*
import baaahs.shows.FakeGlContext
import baaahs.shows.FakeShowPlayer
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

@Suppress("unused")
object PatchResolverSpec : Spek({
    describe("Layering of patch links") {
        fun autoWire(vararg shaders: Shader, shaderChannel: ShaderChannel = ShaderChannel.Main): MutablePatchSet {
            return testToolchain.autoWire(*shaders, shaderChannel = shaderChannel)
                .acceptSuggestedLinkOptions().confirm()
        }

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
                editLayouts { panels["main"] = mainPanel }
            }
        }
        val show by value {
            val show = mutableShow.build(ShowBuilder())
            ShowOpener(testToolchain, show, FakeShowPlayer()).openShow()
        }
        val linkedPatch by value { generateLinkedPatch(show.allDataSources, show.buildActivePatchSet()) }

        fun clickButton(id: String) {
            (show.allControls.associateBy { it.id }.getBang(id, "control") as OpenButtonControl)
                .click()
        }

        context("for a show with a couple buttons") {
            beforeEachTest {
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
                clickButton("brightnessButton")
                clickButton("orangeButton")

                kexpect(linkedPatch.toGlsl()).toBe(
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
                        };

                        struct ModelInfo {
                            vec3 center;
                            vec3 extents;
                        };
                        
                        // Data source: Brightness Slider
                        uniform float in_brightnessSlider;

                        // Data source: Fixture Info
                        uniform FixtureInfo in_fixtureInfo;

                        // Data source: Model Info
                        uniform ModelInfo in_modelInfo;

                        // Data source: Pixel Location
                        uniform sampler2D ds_pixelLocation_texture;
                        vec3 ds_pixelLocation_getPixelCoords(vec2 rasterCoord) {
                            vec3 xyzInEntity = texelFetch(ds_pixelLocation_texture, ivec2(rasterCoord.xy), 0).xyz;
                            vec4 xyzwInModel = in_fixtureInfo.transformation * vec4(xyzInEntity, 1.);
                            return xyzwInModel.xyz;
                        }
                        vec3 in_pixelLocation;

                        // Data source: Time
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

                        // Shader: Orange Shader; namespace: p1
                        // Orange Shader

                        vec4 p1_orangeShader_gl_FragColor = vec4(0., 0., 0., 1.);

                        #line 2 1
                        void p1_orangeShader_main() {
                          p1_orangeShader_gl_FragColor = vec4(1., .5, in_time, p0_cylindricalProjectioni_result.x);
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

                            // Invoke Orange Shader
                            p1_orangeShader_main();

                            // Invoke Brightness Filter
                            p2_brightnessFilteri_result = p2_brightnessFilter_main(p1_orangeShader_gl_FragColor);

                            sm_result = p2_brightnessFilteri_result;
                        }
                        
                    """.trimIndent()
                )
            }

            context("with a data source filter") {
                beforeEachTest {
                    mutableShow.apply {
                        addButton(mainPanel, "Time Wobble") {
                            addPatch(autoWire(wobblyTimeFilter, shaderChannel = ShaderChannel("time")).apply {
                                mutablePatches.only("shader instance")
                                    .incomingLinks["time"] = MutableDataSourcePort(TimeDataSource())
                            })
                        }
                    }
                }

                it("merges layered patches into a single patch") {
                    clickButton("brightnessButton")
                    clickButton("orangeButton")
                    clickButton("timeWobbleButton")

                    kexpect(linkedPatch.toGlsl()).toBe(
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
                        };

                        struct ModelInfo {
                            vec3 center;
                            vec3 extents;
                        };
                        
                        // Data source: Brightness Slider
                        uniform float in_brightnessSlider;

                        // Data source: Fixture Info
                        uniform FixtureInfo in_fixtureInfo;

                        // Data source: Model Info
                        uniform ModelInfo in_modelInfo;

                        // Data source: Pixel Location
                        uniform sampler2D ds_pixelLocation_texture;
                        vec3 ds_pixelLocation_getPixelCoords(vec2 rasterCoord) {
                            vec3 xyzInEntity = texelFetch(ds_pixelLocation_texture, ivec2(rasterCoord.xy), 0).xyz;
                            vec4 xyzwInModel = in_fixtureInfo.transformation * vec4(xyzInEntity, 1.);
                            return xyzwInModel.xyz;
                        }
                        vec3 in_pixelLocation;

                        // Data source: Time
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

                        // Shader: Orange Shader; namespace: p2
                        // Orange Shader

                        vec4 p2_orangeShader_gl_FragColor = vec4(0., 0., 0., 1.);

                        #line 2 2
                        void p2_orangeShader_main() {
                          p2_orangeShader_gl_FragColor = vec4(1., .5, p1_wobblyTimeFilteri_result, p0_cylindricalProjectioni_result.x);
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
            beforeEachTest {
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
                            ), shaderChannel = ShaderChannel("main")
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
                                add(PortLinkOption(MutableShaderChannel("other")))
                            }
                        }.acceptSuggestedLinkOptions().confirm()
                    )
                    addPatch(autoWire(orangeShader, shaderChannel = ShaderChannel("other")))
                    addPatch(autoWire(wobblyTimeFilter, shaderChannel = ShaderChannel("time")))
                }
            }

            it("merges layered patches into a single patch") {
                kexpect(linkedPatch.toGlsl()).toBe(
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
                        };

                        struct ModelInfo {
                            vec3 center;
                            vec3 extents;
                        };

                        // Data source: Fade Slider
                        uniform float in_fadeSlider;

                        // Data source: Fixture Info
                        uniform FixtureInfo in_fixtureInfo;

                        // Data source: Model Info
                        uniform ModelInfo in_modelInfo;

                        // Data source: Pixel Location
                        uniform sampler2D ds_pixelLocation_texture;
                        vec3 ds_pixelLocation_getPixelCoords(vec2 rasterCoord) {
                            vec3 xyzInEntity = texelFetch(ds_pixelLocation_texture, ivec2(rasterCoord.xy), 0).xyz;
                            vec4 xyzwInModel = in_fixtureInfo.transformation * vec4(xyzInEntity, 1.);
                            return xyzwInModel.xyz;
                        }
                        vec3 in_pixelLocation;

                        // Data source: Time
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

                        // Shader: A Main Shader; namespace: p2
                        // A Main Shader

                        vec4 p2_aMainShader_gl_FragColor = vec4(0., 0., 0., 1.);

                        #line 2 2
                        void p2_aMainShader_main() {
                          p2_aMainShader_gl_FragColor = vec4(p1_wobblyTimeFilteri_result, p1_wobblyTimeFilteri_result, p1_wobblyTimeFilteri_result, p0_cylindricalProjectioni_result.x);
                        }

                        // Shader: Orange Shader; namespace: p3
                        // Orange Shader

                        vec4 p3_orangeShader_gl_FragColor = vec4(0., 0., 0., 1.);

                        #line 2 3
                        void p3_orangeShader_main() {
                          p3_orangeShader_gl_FragColor = vec4(1., .5, p1_wobblyTimeFilteri_result, p0_cylindricalProjectioni_result.x);
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

                            // Invoke A Main Shader
                            p2_aMainShader_main();

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
})

private fun generateLinkedPatch(dataSources: Map<String, DataSource>, activePatchSet: ActivePatchSet): LinkedPatch {
    val model = TestModel
    val renderManager = RenderManager { FakeGlContext() }
    val fixture = model.allEntities.first()
    val renderTarget = renderManager.addFixture(fakeFixture(1, fixture, model = model))
    val patchResolution = PatchResolver(listOf(renderTarget), activePatchSet, renderManager)
    val portDiagram = patchResolution.portDiagrams
        .getBang(PixelArrayDevice, "fixture type")
        .only("port diagram to render targets")
        .first
    return portDiagram.resolvePatch(ShaderChannel.Main, Color, dataSources)!!
}
