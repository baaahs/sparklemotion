package baaahs.show.mutable

import baaahs.describe
import baaahs.gl.expects
import baaahs.show.DataSource
import baaahs.show.SampleData
import baaahs.show.Surfaces
import org.spekframework.spek2.Spek

object MutableShowVisitorSpec : Spek({
    val show by value { MutableShow(SampleData.sampleLegacyShow) }

    describe<MutableShowVisitor> {
        val visitor by value { CollectingVisitor() }
        val visitorCounts by value {
            visitor.items.groupBy { it }.mapValues { (_, v) -> v.size }
        }

        context("when visiting a show") {
            it("should visit things but only once") {
                show.accept(visitor)

                expects(
                    mapOf(
                        "Some patch holder" to 8, // These represent distinct items that don't have distinguishing short names.
                        "Some patch" to 9, // These represent distinct items that don't have distinguishing short names.
                        "Control Backdrops" to 2, // There really are two controls named "Backdrops"
                        "Control Blue Aqua Green" to 1,
                        "Control Brightness" to 1,
                        "Control Checkerboard Size" to 1,
                        "Control Checkerboard" to 1,
                        "Control Color" to 1,
                        "Control Fire" to 1,
                        "Control Holocene" to 1,
                        "Control Intensity" to 1,
                        "Control Pleistocene" to 1,
                        "Control Red Yellow Green" to 1,
                        "Control Saturation" to 1,
                        "Control Scenes" to 1,
                        "Control Ripple" to 1,
                        "Data source Brightness Slider" to 1,
                        "Data source Checkerboard Size Slider" to 1,
                        "Data source Model Info" to 1,
                        "Data source Pixel Location" to 1,
                        "Data source Redness Slider" to 1,
                        "Data source Resolution" to 1,
                        "Data source Saturation Slider" to 1,
                        "Data source Time" to 1,
                        "Data source Ripple Amount Slider" to 1,
                        "Shader Another GLSL Hue Test Pattern" to 1,
                        "Shader Brightness" to 1,
                        "Shader Checkerboard" to 1,
                        "Shader Darkness" to 1,
                        "Shader Fire Ball" to 1,
                        "Shader GLSL Hue Test Pattern" to 1,
                        "Shader Ripple" to 1,
                        "Shader Saturation" to 1,
                        "Shader XY Projection" to 1,
                        "Stream main" to 1,
                        "Surfaces All Surfaces" to 1,
                    )
                ) { visitorCounts }
            }
        }
    }
})

class CollectingVisitor : MutableShowVisitor {
    val items = mutableListOf<String>()

    override fun visit(mutablePatchHolder: MutablePatchHolder) {
        items.add("Some patch holder")
    }

    override fun visit(mutablePatch: MutablePatch) {
        items.add("Some patch")
    }

    override fun visit(surfaces: Surfaces) {
        items.add("Surfaces ${surfaces.name}")
    }

    override fun visit(mutableControl: MutableControl) {
        items.add("Control ${mutableControl.title}")
    }

    override fun visit(mutableShader: MutableShader) {
        items.add("Shader ${mutableShader.title}")
    }

    override fun visit(mutableStream: MutableStream) {
        items.add("Stream ${mutableStream.id}")
    }

    override fun visit(dataSource: DataSource) {
        items.add("Data source ${dataSource.title}")
    }
}