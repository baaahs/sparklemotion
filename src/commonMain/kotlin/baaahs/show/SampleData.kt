package baaahs.show

import baaahs.Color
import baaahs.app.ui.editor.PortLinkOption
import baaahs.gl.RootToolchain
import baaahs.gl.autoWire
import baaahs.glsl.Shaders
import baaahs.plugin.Plugins
import baaahs.plugin.core.feed.ColorPickerFeed
import baaahs.plugin.core.feed.SliderFeed
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutablePort
import baaahs.show.mutable.MutableShow
import baaahs.show.mutable.editor

object SampleData {
    val plugins = Plugins.safe(Plugins.dummyContext)
    private val toolchain = RootToolchain(plugins)
    private val uvShader get() = wireUp(Shaders.xyProjection)

    private val showDefaultPaint = toolchain.autoWire(
        Shader(
            "Darkness",
            /**language=glsl*/
            """
                void main(void) {
                    gl_FragColor = vec4(0., 0., 0., 1.);
                }
            """.trimIndent()
        )
    )
        .acceptSuggestedLinkOptions()
        .confirm()

    private val brightnessFilter = toolchain.autoWire(
        Shader(
            "Brightness",
            /**language=glsl*/
            """
                uniform float brightness; // @@Slider min=0 max=1.25 default=1
                
                // @return color
                // @param inColor color
                vec4 main(vec4 inColor) {
                    vec4 clampedColor = clamp(inColor, 0., 1.);
                    return vec4(clampedColor.rgb * brightness, clampedColor.a);
                }
            """.trimIndent()
        )
    )
        .acceptSuggestedLinkOptions()
        .confirm()

    private val saturationFilter = toolchain.autoWire(
        Shader(
            "Saturation",
            /**language=glsl*/
            """
                uniform float saturation; // @@Slider min=0 max=1.25 default=1
    
                // All components are in the range [0…1], including hue.
                vec3 rgb2hsv(vec3 c)
                {
                    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
                    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
                    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));
    
                    float d = q.x - min(q.w, q.y);
                    float e = 1.0e-10;
                    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
                }
                 
    
                // All components are in the range [0…1], including hue.
                vec3 hsv2rgb(vec3 c)
                {
                    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
                    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
                    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
                }
                
                // @return color
                // @param inColor color
                vec4 main(vec4 inColor) {
                    if (saturation == 1.) return inColor;
    
                    vec4 clampedColor = clamp(inColor, 0., 1.);
                    vec3 hsv = rgb2hsv(clampedColor.rgb);
                    hsv.y *= saturation;
                    return vec4(hsv2rgb(hsv), clampedColor.a);
                }
            """.trimIndent()
        )
    )
        .acceptSuggestedLinkOptions()
        .confirm()

    private val redYellowGreenPatch = toolchain.autoWire(
        Shader(
            "GLSL Hue Test Pattern",
            /**language=glsl*/
            """
                uniform vec2 resolution;
                void main(void) {
                    gl_FragColor = vec4(gl_FragCoord.xy / resolution, 0.0, 1.0);
                }
            """.trimIndent()
        )
    )
        .acceptSuggestedLinkOptions()
        .confirm()

    private val blueAquaGreenPatch = toolchain.autoWire(
        Shader(
            "Another GLSL Hue Test Pattern",
            /**language=glsl*/
            """
                uniform vec2 resolution;
                uniform float redness;
                void main(void) {
                    gl_FragColor = vec4(redness, gl_FragCoord.xy / resolution, 1.0);
                }
            """.trimIndent()
        )
    )
        .acceptSuggestedLinkOptions()
        .confirm()

    private val fireBallPatch = toolchain.autoWire(FixtureShaders.fireBallGlsl)
        .acceptSuggestedLinkOptions()
        .confirm()

    private val headlightsMode = toolchain.autoWire(
        Shader(
            "Headlights Mode",
            /**language=glsl*/
            """
                const float PI = 3.141592654;
                const float panScale = 540. / PI / 180.;
                const float tiltScale = 125. / PI / 180.;

                struct FixtureInfo {
                    vec3 position;
                    vec3 rotation;
                    mat4 transformation;
                    vec3 boundaryMin;
                    vec3 boundaryMax;
                };

                struct MovingHeadParams {
                    float pan;
                    float tilt;
                    float colorWheel;
                    float dimmer;
                };

                uniform FixtureInfo fixtureInfo;

                uniform float targetX; // @@Slider default=240 max=500 min=-100
                uniform float targetY; // @@Slider default=0 max=400 min=0
                uniform float targetZ; // @@Slider default=0 max=200 min=-200

                // @param params moving-head-params
                void main(out MovingHeadParams params) {
                    vec3 target = vec3(targetX, targetY, targetZ);

                    vec3 direction = normalize((vec4(target, 1.0) * fixtureInfo.transformation).xyz);
                    float theta = atan(direction.y, direction.x);
                    float phi = acos(direction.z);
                    
                    params.pan = theta * panScale;
                    params.tilt = phi * tiltScale + .5;
                    params.colorWheel = 0.;
                    params.dimmer = 1.;
                }
            """.trimIndent()
        )
    )
        .acceptSuggestedLinkOptions()
        .confirm()

    val sampleShow: Show get() = createSampleShow().getShow()

    fun createSampleShow(withHeadlightsMode: Boolean = false) = MutableShow("Sample Show") {
        addPatch(uvShader)
        addPatch(showDefaultPaint)
        addPatch(brightnessFilter)
        addPatch(saturationFilter)

        val color = ColorPickerFeed("Color", Color.WHITE)
        val brightness = SliderFeed(
            "Brightness", 1f, 0f, 1.25f, null
        )
        val saturation = SliderFeed(
            "Saturation", 1f, 0f, 1.25f, null
        )
        val checkerboardSize = SliderFeed(
            "Checkerboard Size", .1f, .001f, 1f, null
        )

        editLayouts {
            editLayout("default") {
                addTab("Main") {
                    columns = 18
                    rows = 8

                    addButtonGroup("Backdrops", 0, 0, 4, 8, this@MutableShow) {
                        columns = 2

                        addButton("Red Yellow Green", 0, 0, mutableShow = this@MutableShow) {
                            addPatch(redYellowGreenPatch)
                        }

                        addButton("Fire", 1, 0, mutableShow = this@MutableShow) {
                            addPatch(fireBallPatch)
                        }

                        addButton("Blue Aqua Green", 0, 1, mutableShow = this@MutableShow) {
                            addPatch(blueAquaGreenPatch)
                        }

                        addButton("Checkerboard", 1, 1, mutableShow = this@MutableShow) {
                            addPatch(
                                wireUp(
                                    Shaders.checkerboard,
                                    mapOf("checkerboardSize" to checkerboardSize.editor())
                                )
                            )
                        }
                    }

                    addControl(color.buildControl(), 4, 6, 3, 2)
                    addControl(brightness.buildControl(), 7, 6, 1, 2)
                    addControl(saturation.buildControl(), 8, 6, 1, 2)

                    addButton("Ripple", 4, 0, 2, mutableShow = this@MutableShow) {
                        addPatch(wireUp(Shaders.ripple))
                    }

                    if (withHeadlightsMode) {
                        addButton("Headlights Mode", 4, 2, 2, mutableShow = this@MutableShow) {
                            addPatch(headlightsMode)
                        }
                    }

                    addVisualizer(12, 0, 6, 3)
                    addVacuity(4, 4, 14, 2)
                }
            }
        }
    }

    private fun wireUp(shader: Shader, ports: Map<String, MutablePort> = emptyMap()): MutablePatch {
        val unresolvedPatch = toolchain.autoWire(shader)
        unresolvedPatch.apply {
            ports.forEach { (portId, port) ->
                linkOptionsFor(portId).apply {
                    clear()
                    add(PortLinkOption(port))
                }
            }
        }
        return unresolvedPatch
            .acceptSuggestedLinkOptions()
            .confirm()
    }
}
