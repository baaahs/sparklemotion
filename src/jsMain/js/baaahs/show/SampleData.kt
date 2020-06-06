package baaahs.show

import baaahs.glshaders.AutoWirer
import baaahs.ui.firstItem
import baaahs.ui.firstSplit
import baaahs.ui.secondItem
import baaahs.ui.secondSplit
import external.mosaic.MosaicParent
import kotlinext.js.jsObject

object SampleData {
    val stdLayout = jsObject<MosaicParent<String>> {
        direction = "row"
        splitPercentage = 70

        firstSplit = jsObject {
            direction = "column"
            splitPercentage = 20

            firstItem = "Scenes"

            secondSplit = jsObject {
                direction = "column"
                splitPercentage = 70

                firstItem = "Patches"
                secondItem = "More Controls"
            }
        }

        secondSplit = jsObject {
            direction = "column"
            splitPercentage = 20

            firstItem = "Preview"
            secondItem = "Controls"
        }
    }

    val samplePatch = AutoWirer().autoWire("""
        // GLSL Hue Test Pattern
        uniform vec2 resolution;
        void main(void) {
            gl_FragColor = vec4(gl_FragCoord.xy / resolution, 0.0, 1.0);
        }
    """.trimIndent())

    val samplePatch2 = AutoWirer().autoWire("""
        // GLSL Hue Test Pattern
        uniform vec2 resolution;
        void main(void) {
            gl_FragColor = vec4(0.0, gl_FragCoord.xy / resolution, 1.0);
        }
    """.trimIndent())

    val defaultLayout = Layout(stdLayout)
    val layouts = Layouts(
        listOf("Scenes", "Patches", "More Controls", "Preview", "Controls"),
        mapOf("default" to defaultLayout)
    )

    val scenesControl = Control("Scenes", "SceneList", emptyMap())
    val patchesControl = Control("Patches", "PatchList", emptyMap())
    val colorControl = Control("Color", "ColorPicker", emptyMap())
    val brightnessControl = Control("Brightness", "Slider", emptyMap())
    val bzzztinessControl = Control("Brightness", "Slider", emptyMap())

    val sampleShow = Show(
        name ="Xian's Show",
        scenes = listOf(
            Scene(
                name = "Pleistocene",
                patchSets = listOf(
                    PatchSet(
                        name = "Ancient Baby Rave",
                        patchMappings = listOf(PatchMapping(samplePatch, Surfaces("All Surfaces"))),
                        eventBindings = listOf(),
                        controlLayout = mapOf()
                    ),
                    PatchSet(
                        name = "BZZZT",
                        patchMappings = listOf(PatchMapping(samplePatch2, Surfaces("All Surfaces"))),
                        eventBindings = listOf(),
                        controlLayout = mapOf(
                            "Patches" to listOf(bzzztinessControl)
                        )
                    )
                ),
                eventBindings = listOf(),
                controlLayout = mapOf()
            ),
            Scene(
                name = "Holocene",
                patchSets = listOf(
                    PatchSet(
                        name = "Baby Rave",
                        patchMappings = listOf(PatchMapping(samplePatch2, Surfaces("All Surfaces"))),
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
        controls = listOf(scenesControl, patchesControl, colorControl, brightnessControl, bzzztinessControl),
        layouts = layouts,
        controlLayout = mapOf(
            "Scenes" to listOf(scenesControl),
            "Patches" to listOf(patchesControl),
            "More Controls" to listOf(colorControl, brightnessControl)
        )
    )
}
