package baaahs.gl.patch

import baaahs.TestModel
import baaahs.app.ui.editor.PortLinkOption
import baaahs.fixtures.PixelArrayDevice
import baaahs.getBang
import baaahs.gl.kexpect
import baaahs.gl.patch.ContentType.Companion.Color
import baaahs.gl.render.RenderManager
import baaahs.gl.shader.FilterShader
import baaahs.gl.shader.GenericPaintShader
import baaahs.gl.testPlugins
import baaahs.glsl.Shaders
import baaahs.only
import baaahs.shaders.fakeFixture
import baaahs.show.DataSource
import baaahs.show.Shader
import baaahs.show.ShaderChannel
import baaahs.show.live.ActivePatchSet
import baaahs.show.live.OpenButtonControl
import baaahs.show.live.ShowOpener
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutableShaderChannel
import baaahs.show.mutable.MutableShow
import baaahs.show.mutable.ShowBuilder
import baaahs.shows.FakeGlContext
import baaahs.shows.FakeShowPlayer
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

@Suppress("unused")
object PatchResolverSpec : Spek({
    describe("Layering of patch links") {
        val autoWirer by value { AutoWirer(testPlugins()) }

        fun autoWire(vararg shaders: Shader, shaderChannel: ShaderChannel = ShaderChannel.Main): MutablePatch {
            return autoWirer.autoWire(*shaders, shaderChannel = shaderChannel)
                .acceptSuggestedLinkOptions().confirm()
        }

        val uvShader = Shaders.cylindricalProjection
        val blackShader by value {
            Shader(
                "Black Shader", GenericPaintShader,
                "void main() {\n  gl_FragColor = vec4(0.);\n}"
            )
        }
        val orangeShader by value {
            Shader(
                "Orange Shader", GenericPaintShader,
                "uniform float time;\n" +
                        "void main() {\n" +
                        "  gl_FragColor = vec4(1., .5, time, gl_FragCoord.x);\n" +
                        "}"
            )
        }
        val brightnessFilter by value {
            Shader(
                "Brightness Filter", FilterShader,
                "uniform float brightness; // @@Slider min=0 max=1 default=1\n" +
                        "vec4 mainFilter(vec4 colorIn) {\n" +
                        "  return colorIn * brightness;\n" +
                        "}"
            )
        }
        val wobblyTimeFilter by value {
            Shader(
                "Wobbly Time Filter", prototype = null, resultContentType = ContentType.Time,
                "uniform float time; // @type time\n" +
                        "// @type time\n" +
                        "float main() { return time + sin(time); }"
            )
        }
        val mutableShow by value {
            MutableShow("test show") {
                editLayouts { panelNames = mutableListOf("Main") }
            }
        }
        val show by value {
            val show = mutableShow.build(ShowBuilder())
            ShowOpener(autoWirer.glslAnalyzer, show, FakeShowPlayer()).openShow()
        }
        val linkedPatch by value { generateLinkedPatch(show.allDataSources, show.activePatchSet()) }

        fun clickButton(id: String) {
            (show.allControls.associateBy { it.id }.getBang(id, "control") as OpenButtonControl)
                .click()
        }

        context("for a show with a couple buttons") {
            beforeEachTest {
                mutableShow.apply {
                    addPatch(autoWire(uvShader, blackShader))

                    addButton("Main", "Brightness") {
                        addPatch(autoWire(brightnessFilter))
                    }

                    addButton("Main", "Orange") {
                        addPatch(autoWire(orangeShader))
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

                        struct ModelInfo {
                            vec3 center;
                            vec3 extents;
                        };
                        
                        // Data source: Brightness Slider
                        uniform float in_brightnessSlider;

                        // Data source: Model Info
                        uniform ModelInfo in_modelInfo;

                        // Data source: Pixel Location
                        uniform sampler2D ds_pixelLocation_texture;
                        vec3 ds_pixelLocation_getPixelCoords(vec2 rasterCoord) {
                            return texelFetch(ds_pixelLocation_texture, ivec2(rasterCoord.xy), 0).xyz;
                        }
                        vec3 in_pixelLocation;

                        // Data source: Time
                        uniform float in_time;

                        // Shader: Cylindrical Projection; namespace: p0
                        // Cylindrical Projection

                        vec2 p0_cylindricalProjectioni_result = vec2(0.);

                        #line 10
                        const float p0_cylindricalProjection_PI = 3.141592654;

                        #line 12
                        vec2 p0_cylindricalProjection_mainProjection(vec3 pixelLocation) {
                            vec3 pixelOffset = pixelLocation - in_modelInfo.center;
                            vec3 normalDelta = normalize(pixelOffset);
                            float theta = atan(abs(normalDelta.z), normalDelta.x); // theta in range [-π,π]
                            if (theta < 0.0) theta += (2.0f * p0_cylindricalProjection_PI);                 // theta in range [0,2π)
                            float u = theta / (2.0f * p0_cylindricalProjection_PI);                         // u in range [0,1)
                            float v = (pixelOffset.y + in_modelInfo.extents.y / 2.0f) / in_modelInfo.extents.y;
                            return vec2(u, v);
                        }

                        // Shader: Orange Shader; namespace: p1
                        // Orange Shader

                        vec4 p1_orangeShader_gl_FragColor = vec4(0., 0., 0., 1.);

                        #line 2
                        void p1_orangeShader_main() {
                          p1_orangeShader_gl_FragColor = vec4(1., .5, in_time, p0_cylindricalProjectioni_result.x);
                        }

                        // Shader: Brightness Filter; namespace: p2
                        // Brightness Filter

                        vec4 p2_brightnessFilteri_result = vec4(0., 0., 0., 1.);

                        #line 1
                         vec4 p2_brightnessFilter_mainFilter(vec4 colorIn) {
                          return colorIn * in_brightnessSlider;
                        }


                        #line 10001
                        void main() {
                          // Invoke Pixel Location
                          in_pixelLocation = ds_pixelLocation_getPixelCoords(gl_FragCoord.xy);

                          // Invoke Cylindrical Projection
                          p0_cylindricalProjectioni_result = p0_cylindricalProjection_mainProjection(in_pixelLocation);

                          // Invoke Orange Shader
                          p1_orangeShader_main();

                          // Invoke Brightness Filter
                          p2_brightnessFilteri_result = p2_brightnessFilter_mainFilter(p1_orangeShader_gl_FragColor);

                          sm_result = p2_brightnessFilteri_result;
                        }
                        
                    """.trimIndent()
                )
            }

            context("with a data source filter") {
                beforeEachTest {
                    mutableShow.apply {
                        addButton("Main", "Time Wobble") {
                            addPatch(
                                autoWire(wobblyTimeFilter).apply {
                                    mutableShaderInstances.only("shader instance")
                                        .shaderChannel = MutableShaderChannel("time")
                                }
                            )
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

                        struct ModelInfo {
                            vec3 center;
                            vec3 extents;
                        };
                        
                        // Data source: Brightness Slider
                        uniform float in_brightnessSlider;

                        // Data source: Model Info
                        uniform ModelInfo in_modelInfo;

                        // Data source: Pixel Location
                        uniform sampler2D ds_pixelLocation_texture;
                        vec3 ds_pixelLocation_getPixelCoords(vec2 rasterCoord) {
                            return texelFetch(ds_pixelLocation_texture, ivec2(rasterCoord.xy), 0).xyz;
                        }
                        vec3 in_pixelLocation;

                        // Data source: Time
                        uniform float in_time;

                        // Shader: Cylindrical Projection; namespace: p0
                        // Cylindrical Projection

                        vec2 p0_cylindricalProjectioni_result = vec2(0.);

                        #line 10
                        const float p0_cylindricalProjection_PI = 3.141592654;

                        #line 12
                        vec2 p0_cylindricalProjection_mainProjection(vec3 pixelLocation) {
                            vec3 pixelOffset = pixelLocation - in_modelInfo.center;
                            vec3 normalDelta = normalize(pixelOffset);
                            float theta = atan(abs(normalDelta.z), normalDelta.x); // theta in range [-π,π]
                            if (theta < 0.0) theta += (2.0f * p0_cylindricalProjection_PI);                 // theta in range [0,2π)
                            float u = theta / (2.0f * p0_cylindricalProjection_PI);                         // u in range [0,1)
                            float v = (pixelOffset.y + in_modelInfo.extents.y / 2.0f) / in_modelInfo.extents.y;
                            return vec2(u, v);
                        }

                        // Shader: Wobbly Time Filter; namespace: p1
                        // Wobbly Time Filter

                        float p1_wobblyTimeFilteri_result = float(0.);

                        #line 1
                         
                        float p1_wobblyTimeFilter_main() { return in_time + sin(in_time); }

                        // Shader: Orange Shader; namespace: p2
                        // Orange Shader

                        vec4 p2_orangeShader_gl_FragColor = vec4(0., 0., 0., 1.);

                        #line 2
                        void p2_orangeShader_main() {
                          p2_orangeShader_gl_FragColor = vec4(1., .5, p1_wobblyTimeFilteri_result, p0_cylindricalProjectioni_result.x);
                        }

                        // Shader: Brightness Filter; namespace: p3
                        // Brightness Filter

                        vec4 p3_brightnessFilteri_result = vec4(0., 0., 0., 1.);

                        #line 1
                         vec4 p3_brightnessFilter_mainFilter(vec4 colorIn) {
                          return colorIn * in_brightnessSlider;
                        }


                        #line 10001
                        void main() {
                          // Invoke Pixel Location
                          in_pixelLocation = ds_pixelLocation_getPixelCoords(gl_FragCoord.xy);

                          // Invoke Cylindrical Projection
                          p0_cylindricalProjectioni_result = p0_cylindricalProjection_mainProjection(in_pixelLocation);

                          // Invoke Wobbly Time Filter
                          p1_wobblyTimeFilteri_result = p1_wobblyTimeFilter_main();

                          // Invoke Orange Shader
                          p2_orangeShader_main();

                          // Invoke Brightness Filter
                          p3_brightnessFilteri_result = p3_brightnessFilter_mainFilter(p2_orangeShader_gl_FragColor);

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
                                "Main Shader", GenericPaintShader,
                                "uniform float time;\n" +
                                        "void main() {\n" +
                                        "  gl_FragColor = vec4(time, time, time, gl_FragCoord.x);\n" +
                                        "}"
                            ), shaderChannel = ShaderChannel("main")
                        )
                    )
                    addPatch(
                        autoWirer.autoWire(
                            Shader(
                                "Fade", FilterShader, """
                                    uniform float fade;
                                    varying vec4 otherColorStream; // @type color
                
                                    vec4 mainFilter(vec4 colorIn) {
                                        return mix(colorIn, otherColorStream, fade);
                                    }
                                """.trimIndent()
                            )
                        ).editAll {
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

                        struct ModelInfo {
                            vec3 center;
                            vec3 extents;
                        };

                        // Data source: Fade Slider
                        uniform float in_fadeSlider;

                        // Data source: Model Info
                        uniform ModelInfo in_modelInfo;

                        // Data source: Pixel Location
                        uniform sampler2D ds_pixelLocation_texture;
                        vec3 ds_pixelLocation_getPixelCoords(vec2 rasterCoord) {
                            return texelFetch(ds_pixelLocation_texture, ivec2(rasterCoord.xy), 0).xyz;
                        }
                        vec3 in_pixelLocation;

                        // Data source: Time
                        uniform float in_time;

                        // Shader: Cylindrical Projection; namespace: p0
                        // Cylindrical Projection

                        vec2 p0_cylindricalProjectioni_result = vec2(0.);

                        #line 10
                        const float p0_cylindricalProjection_PI = 3.141592654;

                        #line 12
                        vec2 p0_cylindricalProjection_mainProjection(vec3 pixelLocation) {
                            vec3 pixelOffset = pixelLocation - in_modelInfo.center;
                            vec3 normalDelta = normalize(pixelOffset);
                            float theta = atan(abs(normalDelta.z), normalDelta.x); // theta in range [-π,π]
                            if (theta < 0.0) theta += (2.0f * p0_cylindricalProjection_PI);                 // theta in range [0,2π)
                            float u = theta / (2.0f * p0_cylindricalProjection_PI);                         // u in range [0,1)
                            float v = (pixelOffset.y + in_modelInfo.extents.y / 2.0f) / in_modelInfo.extents.y;
                            return vec2(u, v);
                        }

                        // Shader: Wobbly Time Filter; namespace: p1
                        // Wobbly Time Filter

                        float p1_wobblyTimeFilteri_result = float(0.);

                        #line 1
                         
                        float p1_wobblyTimeFilter_main() { return in_time + sin(in_time); }

                        // Shader: Main Shader; namespace: p2
                        // Main Shader

                        vec4 p2_mainShader_gl_FragColor = vec4(0., 0., 0., 1.);

                        #line 2
                        void p2_mainShader_main() {
                          p2_mainShader_gl_FragColor = vec4(p1_wobblyTimeFilteri_result, p1_wobblyTimeFilteri_result, p1_wobblyTimeFilteri_result, p0_cylindricalProjectioni_result.x);
                        }

                        // Shader: Orange Shader; namespace: p3
                        // Orange Shader

                        vec4 p3_orangeShader_gl_FragColor = vec4(0., 0., 0., 1.);

                        #line 2
                        void p3_orangeShader_main() {
                          p3_orangeShader_gl_FragColor = vec4(1., .5, p1_wobblyTimeFilteri_result, p0_cylindricalProjectioni_result.x);
                        }

                        // Shader: Fade; namespace: p4
                        // Fade

                        vec4 p4_fadei_result = vec4(0., 0., 0., 1.);

                        #line 2
                         
                        vec4 p4_fade_mainFilter(vec4 colorIn) {
                            return mix(colorIn, p3_orangeShader_gl_FragColor, in_fadeSlider);
                        }


                        #line 10001
                        void main() {
                          // Invoke Pixel Location
                          in_pixelLocation = ds_pixelLocation_getPixelCoords(gl_FragCoord.xy);

                          // Invoke Cylindrical Projection
                          p0_cylindricalProjectioni_result = p0_cylindricalProjection_mainProjection(in_pixelLocation);

                          // Invoke Wobbly Time Filter
                          p1_wobblyTimeFilteri_result = p1_wobblyTimeFilter_main();

                          // Invoke Main Shader
                          p2_mainShader_main();

                          // Invoke Orange Shader
                          p3_orangeShader_main();

                          // Invoke Fade
                          p4_fadei_result = p4_fade_mainFilter(p2_mainShader_gl_FragColor);

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
    val renderManager = RenderManager(model) { FakeGlContext() }
    val fixture = model.allEntities.first()
    val renderTarget = renderManager.addFixture(fakeFixture(1, fixture))
    val patchResolution = PatchResolver(dataSources, renderManager, listOf(renderTarget), activePatchSet)
    val portDiagram = patchResolution.portDiagrams
        .getBang(PixelArrayDevice, "device type")
        .only("port diagram to render targets")
        .first
    return portDiagram.resolvePatch(ShaderChannel.Main, Color)!!
}
