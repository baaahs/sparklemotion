package baaahs.show

import baaahs.Color
import baaahs.glshaders.AutoWirer
import baaahs.glshaders.CorePlugin
import baaahs.glshaders.Plugins

object SampleData {
    val stdLayout = LayoutNode.Columns(
        "100%",
        LayoutNode.Rows(
            "2",
            LayoutNode.Panel("Scenes", "150px"),
            LayoutNode.Columns(
                "3",
                LayoutNode.Panel("Patches", "1"),
                LayoutNode.Rows("3",
                    LayoutNode.Panel("Controls", "1"),
                    LayoutNode.Panel("More Controls", "1")
                )
            ),
            LayoutNode.Panel("Effects", "1")
        ),
        LayoutNode.Rows(
            "1",
            LayoutNode.Panel("Preview", "150px", LayoutNode.Flow.horizontalFromRight),
            LayoutNode.Panel("Adjust", "1.5", LayoutNode.Flow.horizontalFromRight),
            LayoutNode.Panel("Eyes & Movera", "1.5", LayoutNode.Flow.horizontalFromRight),
            LayoutNode.Panel("Transition", "1", LayoutNode.Flow.horizontalFromRight)
        )
    )

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
        defaultLayout.getPanelNames(),
        mapOf("default" to defaultLayout)
    )

    val colorControl = CorePlugin.ColorPickerProvider("Color", Color.WHITE)
    val brightnessControl = CorePlugin.SliderDataSource(
        "Brightness", 1f, 0f, 1f, null
    )
    val intensityControl = CorePlugin.SliderDataSource(
        "Intensity", 1f, 0f, 1f, null
    )

    val sampleShow = ShowEditor("Sample Show").apply {
        editLayouts {
            copyFrom(layouts)
        }

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
                    val from = link.from
                    if (from is DataSourceEditor && from.dataSource is CorePlugin.GadgetDataSource<*>) {
                        addControl("Patches", from.dataSource)
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
