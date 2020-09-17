package baaahs.show

import baaahs.Color
import baaahs.getBang
import baaahs.gl.patch.AutoWirer
import baaahs.glsl.Shaders
import baaahs.plugin.CorePlugin
import baaahs.plugin.Plugins
import baaahs.show.ButtonGroupControl.Direction.Horizontal
import baaahs.show.ButtonGroupControl.Direction.Vertical
import baaahs.show.mutable.MutableDataSource
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutablePort
import baaahs.show.mutable.MutableShow
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

object SampleData {
    val stdLayout = buildJsonObject {
        put("direction", "row")
        put("splitPercentage", 70)

        put("first", buildJsonObject {
            put("direction", "column")
            put("splitPercentage", 20)

            put("first", "Scenes")

            put("second", buildJsonObject {
                put("direction", "column")
                put("splitPercentage", 60)

                put("first", "Backdrops")
                put("second", "More Controls")
            })
        })

        put("second", buildJsonObject {
            put("direction", "column")
            put("splitPercentage", 20)

            put("first", "Preview")
            put("second", "Controls")
        })
    }

    private val plugins = Plugins.safe()
    private val autoWirer = AutoWirer(plugins)

    private val uvShader = wireUp(Shaders.cylindricalProjection)

    private val showDefaultPaint = autoWirer.autoWire(
        Shader(
            "Darkness",
            ShaderType.Paint,
            /**language=glsl*/
            """
                void main(void) {
                    gl_FragColor = vec4(0., 0., 0., 1.);
                }
            """.trimIndent()
        )
    )
        .acceptSymbolicChannelLinks().resolve()

    private val brightnessFilter = autoWirer.autoWire(
        Shader(
            "Brightness",
            ShaderType.Filter,
            /**language=glsl*/
            """
                uniform float brightness; // @@Slider min=0 max=1.25 default=1
    
                vec4 mainFilter(vec4 inColor) {
                    vec4 clampedColor = clamp(inColor, 0., 1.);
                    return vec4(clampedColor.rgb * brightness, clampedColor.a);
                }
            """.trimIndent()
        )
    )
        .acceptSymbolicChannelLinks().resolve()

    private val saturationFilter = autoWirer.autoWire(
        Shader(
            "Saturation",
            ShaderType.Filter,
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
                
                vec4 mainFilter(vec4 inColor) {
                    if (saturation == 1.) return inColor;
    
                    vec4 clampedColor = clamp(inColor, 0., 1.);
                    vec3 hsv = rgb2hsv(clampedColor.rgb);
                    hsv.y *= saturation;
                    return vec4(hsv2rgb(hsv), clampedColor.a);
                }
            """.trimIndent()
        )
    )
        .acceptSymbolicChannelLinks().resolve()

    private val redYellowGreenPatch = autoWirer.autoWire(
        Shader(
            "GLSL Hue Test Pattern",
            ShaderType.Paint,
            /**language=glsl*/
            """
                uniform vec2 resolution;
                void main(void) {
                    gl_FragColor = vec4(gl_FragCoord.xy / resolution, 0.0, 1.0);
                }
            """.trimIndent()
        )
    ).acceptSymbolicChannelLinks().resolve()

    private val blueAquaGreenPatch = autoWirer.autoWire(
        Shader(
            "Another GLSL Hue Test Pattern",
            ShaderType.Paint,
            /**language=glsl*/
            """
                uniform vec2 resolution;
                uniform float redness;
                void main(void) {
                    gl_FragColor = vec4(redness, gl_FragCoord.xy / resolution, 1.0);
                }
            """.trimIndent()
        )
    ).acceptSymbolicChannelLinks().resolve()

    private val fireBallPatch = autoWirer.autoWire(FixtureShaders.fireBallGlsl)
        .acceptSymbolicChannelLinks().resolve()

    val defaultLayout = Layout(stdLayout)
    val layouts = Layouts(
        listOf("Scenes", "Backdrops", "More Controls", "Preview", "Controls"),
        mapOf("default" to defaultLayout)
    )

    val color = CorePlugin.ColorPickerDataSource("Color", Color.WHITE)
    val brightness = CorePlugin.SliderDataSource(
        "Brightness", 1f, 0f, 1.25f, null
    )
    val saturation = CorePlugin.SliderDataSource(
        "Saturation", 1f, 0f, 1.25f, null
    )
    val intensity = CorePlugin.SliderDataSource(
        "Intensity", 1f, 0f, 1f, null
    )
    val checkerboardSize = CorePlugin.SliderDataSource(
        "Checkerboard Size", .1f, .001f, 1f, null
    )

    val sampleShow: Show get() = MutableShow("Sample Show").apply {
        editLayouts {
            copyFrom(layouts)
        }

        addPatch(uvShader)
        addPatch(showDefaultPaint)
        addPatch(brightnessFilter)
        addPatch(saturationFilter)
        addPatch(wireUp(Shaders.flipY))

        addButtonGroup("Scenes", "Scenes", Horizontal) {
            addButton("Pleistocene") {
                addButtonGroup("Backdrops", "Backdrops", Vertical) {
                    addButton("Red Yellow Green") {
                        addPatch(redYellowGreenPatch)
                    }

                    addButton("Fire") {
                        addPatch(fireBallPatch)
                        addControl("Controls", intensity.buildControl())
                    }

                    addButton("Checkerboard") {
                        addPatch(
                            wireUp(
                                Shaders.checkerboard,
                                mapOf("checkerboardSize" to MutableDataSource(checkerboardSize))
                            )
                        )
                        addControl("Controls", checkerboardSize.buildControl())
                    }
                }
            }

            addButton("Holocene") {
                addButtonGroup("Backdrops", "Backdrops", Vertical) {
                    addButton("Blue Aqua Green") {
                        addPatch(blueAquaGreenPatch)
                    }
                }
            }
        }

        addControl("More Controls", color.buildControl())
        addControl("More Controls", brightness.buildControl())
        addControl("More Controls", saturation.buildControl())

        addButton("More Controls", "Wobbly") {
            addPatch(wireUp(Shaders.ripple))
        }
    }.getShow()

    private fun wireUp(shader: Shader, ports: Map<String, MutablePort> = emptyMap()): MutablePatch {
        val unresolvedPatch = autoWirer.autoWire(shader)
        unresolvedPatch.editShader(shader).apply {
            ports.forEach { (portId, port) ->
                incomingLinksOptions.getBang(portId, "port").apply {
                    clear()
                    add(port)
                }
            }
        }
        return unresolvedPatch.acceptSymbolicChannelLinks().resolve()
    }
}
