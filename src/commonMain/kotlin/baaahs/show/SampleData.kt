package baaahs.show

import baaahs.Color
import baaahs.glshaders.AutoWirer
import baaahs.glshaders.CorePlugin
import baaahs.glshaders.Plugins
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
    val redYellowGreenPatch = autoWirer.autoWire(
        """
        // GLSL Hue Test Pattern
        uniform vec2 resolution;
        void main(void) {
            gl_FragColor = vec4(gl_FragCoord.xy / resolution, 0.0, 1.0);
        }
    """.trimIndent()
    )

    val blueAquaGreenPatch = autoWirer.autoWire(
        """
        // Other GLSL Hue Test Pattern
        uniform vec2 resolution;
        uniform float redness;
        void main(void) {
            gl_FragColor = vec4(redness, gl_FragCoord.xy / resolution, 1.0);
        }
    """.trimIndent()
    )

    val fireBallPatch = autoWirer.autoWire(FixtureShaders.fireBallGlsl)

    val defaultLayout = Layout(stdLayout)
    val layouts = Layouts(
        listOf("Scenes", "Patches", "More Controls", "Preview", "Controls"),
        mapOf("default" to defaultLayout)
    )

    val scenesControl = CorePlugin.Scenes("Scenes")
    val patchesControl = CorePlugin.Patches("Patches")
    val colorControl = CorePlugin.ColorPickerProvider("Color", Color.WHITE)
    val brightnessControl = CorePlugin.SliderDataSource(
        "Brightness", 1f, 0f, 1f, 0.01f
    )
    val intensityControl = CorePlugin.SliderDataSource(
        "Intensity", 1f, 0f, 1f, 0.01f
    )

    val sampleShow = ShowEditor("Sample Show").apply {
        this.layouts = SampleData.layouts

        addScene("Pleistocene") {
            addPatchSet("Red Yellow Green") {
                addPatch(redYellowGreenPatch)
            }
            addPatchSet("Fire") {
                addPatch(fireBallPatch)
                addControl("Patches", intensityControl)
            }
        }
        addScene("Holocene") {
            addPatchSet("Blue Aqua Green") {
                addPatch(blueAquaGreenPatch)

                blueAquaGreenPatch.links.forEach { link ->
                    if (link.from is DataSourceEditor && link.from.dataSource is CorePlugin.GadgetDataSource<*>) {
                        addControl("Patches", link.from.dataSource)
                    }
                }
            }
        }

        addControl("Scenes", scenesControl)
        addControl("Patches", patchesControl)
        addControl("More Controls", colorControl)
        addControl("More Controls", brightnessControl)
    }.getShow()
}
