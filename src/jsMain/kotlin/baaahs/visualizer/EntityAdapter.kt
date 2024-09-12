package baaahs.visualizer

import baaahs.model.*
import baaahs.sim.SimulationEnv
import baaahs.visualizer.entity.*
import baaahs.visualizer.geometry.SurfaceGeometry
import baaahs.visualizer.movers.MovingHeadVisualizer

actual class EntityAdapter actual constructor(
    val simulationEnv: SimulationEnv,
    val units: ModelUnit,
    private val isEditing: Boolean
) : Adapter<Model.Entity> {
    actual override fun createVisualizer(entity: Model.Entity): ItemVisualizer<Model.Entity> {
        return (entity.createVisualizer(this) as ItemVisualizer<Model.Entity>)
            .apply { this.isEditing = this@EntityAdapter.isEditing }
    }

    actual fun createEntityGroupVisualizer(objGroup: Model.EntityGroup): ItemVisualizer<Model.EntityGroup> =
        EntityGroupVisualizer(objGroup, this)

    actual fun createLightBarVisualizer(lightBar: LightBar): ItemVisualizer<LightBar> =
        LightBarVisualizer(lightBar, this)

    actual fun createLightRingVisualizer(lightRing: LightRing): ItemVisualizer<LightRing> =
        LightRingVisualizer(lightRing, this, null)

    actual fun createMovingHeadVisualizer(movingHead: MovingHead): ItemVisualizer<MovingHead> =
        MovingHeadVisualizer(movingHead, this)

    actual fun createPolyLineVisualizer(polyLine: PolyLine): ItemVisualizer<PolyLine> =
        PolyLineVisualizer(polyLine, this, null)

    actual fun createSurfaceVisualizer(surface: Model.Surface): ItemVisualizer<Model.Surface> =
        SurfaceVisualizer(surface, this, SurfaceGeometry(surface))
}