package baaahs.visualizer

import baaahs.model.*
import baaahs.sim.SimulationEnv
import baaahs.visualizer.movers.MovingHeadVisualizer

actual class EntityAdapter(
    val simulationEnv: SimulationEnv,
    val isEditing: Boolean = false
) : Adapter<Model.Entity> {
    override fun createVisualizer(entity: Model.Entity): ItemVisualizer<Model.Entity> {
        return (entity.createVisualizer(this) as ItemVisualizer<Model.Entity>)
            .apply { this.isEditing = this@EntityAdapter.isEditing }
    }

    actual fun createLightBarVisualizer(lightBar: LightBar): ItemVisualizer<LightBar> =
        LightBarVisualizer(lightBar, this)

    actual fun createLightRingVisualizer(lightRing: LightRing): ItemVisualizer<LightRing> =
        LightRingVisualizer(lightRing)

    actual fun createMovingHeadVisualizer(movingHead: MovingHead): ItemVisualizer<MovingHead> =
        MovingHeadVisualizer(movingHead, this)

    actual fun createObjGroupVisualizer(objGroup: ObjGroup): ItemVisualizer<ObjGroup> =
        ObjGroupVisualizer(objGroup, this)

    actual fun createPolyLineVisualizer(polyLine: PolyLine): ItemVisualizer<PolyLine> =
        PolyLineVisualizer(polyLine)

    actual fun createSurfaceVisualizer(surface: Model.Surface): ItemVisualizer<Model.Surface> =
        SurfaceVisualizer(surface, SurfaceGeometry(surface))
}