package baaahs.visualizer

import baaahs.model.*
import baaahs.sim.SimulationEnv
import baaahs.visualizer.movers.MovingHeadVisualizer

actual class EntityAdapter(
    val simulationEnv: SimulationEnv
) : Adapter<Model.Entity> {
    override fun createVisualizer(entity: Model.Entity): EntityVisualizer<Model.Entity> {
        return entity.createVisualizer(this).also {
            val obj = it.obj
            obj.modelEntity = entity
        } as EntityVisualizer<Model.Entity>
    }

    actual fun createLightBarVisualizer(lightBar: LightBar): EntityVisualizer<LightBar> =
        LightBarVisualizer(lightBar, this)

    actual fun createLightRingVisualizer(lightRing: LightRing): EntityVisualizer<LightRing> =
        LightRingVisualizer(lightRing)

    actual fun createMovingHeadVisualizer(movingHead: MovingHead): EntityVisualizer<MovingHead> =
        MovingHeadVisualizer(movingHead, this)

    actual fun createObjGroupVisualizer(objGroup: ObjGroup): EntityVisualizer<ObjGroup> =
        ObjGroupVisualizer(objGroup, this)

    actual fun createPolyLineVisualizer(polyLine: PolyLine): EntityVisualizer<PolyLine> =
        PolyLineVisualizer(polyLine)

    actual fun createSurfaceVisualizer(surface: Model.Surface): EntityVisualizer<Model.Surface> =
        SurfaceVisualizer(surface, SurfaceGeometry(surface))
}