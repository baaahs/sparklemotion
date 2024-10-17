package baaahs.visualizer.movers

import baaahs.model.ModelUnit
import baaahs.model.MovingHeadAdapter
import baaahs.visualizer.VizObj

actual class Cone actual constructor(
    movingHeadAdapter: MovingHeadAdapter,
    units: ModelUnit,
    colorMode: ColorMode
) {
    actual fun addTo(parent: VizObj) {
    }

    actual fun update(state: State) {
    }
}