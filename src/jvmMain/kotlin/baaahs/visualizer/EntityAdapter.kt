package baaahs.visualizer

import baaahs.model.*
import baaahs.sim.SimulationEnv

actual class EntityAdapter actual constructor(
    simulationEnv: SimulationEnv, units: ModelUnit, isEditing: Boolean
) : Adapter<Model.Entity> {
    actual override fun createVisualizer(entity: Model.Entity): ItemVisualizer<Model.Entity> =
        TODO("not implemented")

    actual fun createEntityGroupVisualizer(objGroup: Model.EntityGroup): ItemVisualizer<Model.EntityGroup> =
        TODO("not implemented")

    actual fun createLightBarVisualizer(lightBar: LightBar): ItemVisualizer<LightBar> =
        TODO("not implemented")

    actual fun createLightRingVisualizer(lightRing: LightRing): ItemVisualizer<LightRing> =
        TODO("not implemented")

    actual fun createMovingHeadVisualizer(movingHead: MovingHead): ItemVisualizer<MovingHead> =
        TODO("not implemented")

    actual fun createPolyLineVisualizer(polyLine: PolyLine): ItemVisualizer<PolyLine> =
        TODO("not implemented")

    actual fun createSurfaceVisualizer(surface: Model.Surface): ItemVisualizer<Model.Surface> =
        TODO("not implemented")
}