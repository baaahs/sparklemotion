package baaahs.show

import baaahs.Color
import baaahs.glshaders.AutoWirer
import baaahs.glshaders.CorePlugin
import baaahs.glshaders.Plugins
import baaahs.ports.DataSourceRef
import kotlinx.serialization.json.json

object SampleData {
    val stdLayout = json {
        "direction" to "row"
        "splitPercentage" to 70

        "first" to json {
            "direction" to "column"
            "splitPercentage" to 20

            "first" to "Scenes"

            "second" to json {
                "direction" to "column"
                "splitPercentage" to 70

                "first" to "Patches"
                "second" to "More Controls"
            }
        }

        "second" to json {
            "direction" to "column"
            "splitPercentage" to 20

            "first" to "Preview"
            "second" to "Controls"
        }
    }

    private val plugins = Plugins.findAll()
    private val autoWirer = AutoWirer(plugins)
    val samplePatch = autoWirer.autoWire(
        """
        // GLSL Hue Test Pattern
        uniform vec2 resolution;
        void main(void) {
            gl_FragColor = vec4(gl_FragCoord.xy / resolution, 0.0, 1.0);
        }
    """.trimIndent(), "redGreen"
    )

    val samplePatch2 = autoWirer.autoWire(
        """
        // Other GLSL Hue Test Pattern
        uniform vec2 resolution;
        void main(void) {
            gl_FragColor = vec4(0.0, gl_FragCoord.xy / resolution, 1.0);
        }
    """.trimIndent(), "blueGreen"
    )

    val fireBallPatch = autoWirer.autoWire(SampleShaders.fireBallGlsl, "fire")

    val defaultLayout = Layout(stdLayout)
    val layouts = Layouts(
        listOf("Scenes", "Patches", "More Controls", "Preview", "Controls"),
        mapOf("default" to defaultLayout)
    )

    val scenesControl = CorePlugin.Scenes("scenes", "Scenes")
    val patchesControl = CorePlugin.Patches("patches", "Patches")
    val colorControl = CorePlugin.ColorPickerProvider("color", "Color", Color.WHITE)
    val brightnessControl = CorePlugin.SliderDataSource(
        "brightnessSlider", "Brightness",
        1f, 0f, 1f, 0.01f
    )
    val intensityControl = CorePlugin.SliderDataSource(
        "intensitySlider", "Intensity",
        1f, 0f, 1f, 0.01f
    )

    fun DataSource.ref() = DataSourceRef(id)

    val sampleShow = Show(
        title = "Xian's Show",
        scenes = listOf(
            Scene(
                title = "Pleistocene",
                patchSets = listOf(
                    PatchSet(
                        title = "Red Yellow Green",
                        patchMappings = listOf(
                            PatchMapping(
                                samplePatch.links,
                                Surfaces("All Surfaces")
                            )
                        ),
                        eventBindings = listOf(),
                        controlLayout = mapOf()
                    ),
                    PatchSet(
                        title = "Fire",
                        patchMappings = listOf(
                            PatchMapping(
                                fireBallPatch.links,
                                Surfaces("All Surfaces")
                            )
                        ),
                        eventBindings = listOf(),
                        controlLayout = mapOf(
                            "Patches" to listOf(intensityControl.ref())
                        )
                    )
                ),
                eventBindings = listOf(),
                controlLayout = mapOf()
            ),
            Scene(
                title = "Holocene",
                patchSets = listOf(
                    PatchSet(
                        title = "Blue Aqua Green",
                        patchMappings = listOf(
                            PatchMapping(
                                samplePatch2.links,
                                Surfaces("All Surfaces")
                            )
                        ),
                        eventBindings = listOf(),
                        controlLayout = mapOf()
                    )
                ),
                eventBindings = listOf(),
                controlLayout = mapOf()
            )
        ),
        patchSets = listOf(),
        eventBindings = listOf(),
        dataSources = listOf(
            scenesControl,
            patchesControl,
            colorControl,
            brightnessControl,
            intensityControl,
            CorePlugin.Resolution("resolution"),
            CorePlugin.Time("time"),
            CorePlugin.UvCoordTexture("uvCoordsTexture")
        ),
        layouts = layouts,
        controlLayout = mapOf(
            "Scenes" to listOf(scenesControl.ref()),
            "Patches" to listOf(patchesControl.ref()),
            "More Controls" to listOf(
                colorControl.ref(),
                brightnessControl.ref()
            )
        ),
        shaderFragments = (samplePatch.components + samplePatch2.components + fireBallPatch.components)
            .entries
            .distinct()
            .associate { (k, v) -> k to v.shaderFragment.src }
    )
}
