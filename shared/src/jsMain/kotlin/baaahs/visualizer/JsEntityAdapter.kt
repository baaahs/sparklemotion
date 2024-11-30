package baaahs.visualizer

import baaahs.model.*
import baaahs.sim.SimulationEnv
import baaahs.visualizer.entity.*
import baaahs.visualizer.geometry.SurfaceGeometry
import baaahs.visualizer.movers.MovingHeadVisualizer

open class JsEntityAdapter(
    override val simulationEnv: SimulationEnv,
    override val units: ModelUnit,
    override val isEditing: Boolean = false
) : EntityAdapter {
    override fun createVisualizer(entity: Model.Entity): ItemVisualizer<Model.Entity> {
        return (entity.createVisualizer(this) as ItemVisualizer<Model.Entity>)
            .apply { this.isEditing = this@JsEntityAdapter.isEditing }
    }

    override fun createEntityGroupVisualizer(objGroup: Model.EntityGroup): ItemVisualizer<Model.EntityGroup> =
        EntityGroupVisualizer(objGroup, this)

    override fun createLightBarVisualizer(lightBar: LightBar): ItemVisualizer<LightBar> =
        LightBarVisualizer(lightBar, this)

    override fun createLightRingVisualizer(lightRing: LightRing): ItemVisualizer<LightRing> =
        LightRingVisualizer(lightRing, this, null)

    override fun createMovingHeadVisualizer(movingHead: MovingHead): ItemVisualizer<MovingHead> =
        MovingHeadVisualizer(movingHead, this)

    override fun createPolyLineVisualizer(polyLine: PolyLine): ItemVisualizer<PolyLine> =
        PolyLineVisualizer(polyLine, this, null)

    override fun createSurfaceVisualizer(surface: Model.Surface): ItemVisualizer<Model.Surface> =
        SurfaceVisualizer(surface, this, SurfaceGeometry(surface))
}

