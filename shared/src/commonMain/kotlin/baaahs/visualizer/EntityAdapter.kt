package baaahs.visualizer

import baaahs.model.*
import baaahs.sim.SimulationEnv
import baaahs.visualizer.entity.ItemVisualizer

interface EntityAdapter : Adapter<Model.Entity> {
    val simulationEnv: SimulationEnv
    val units: ModelUnit
    val isEditing: Boolean

    override fun createVisualizer(entity: Model.Entity): ItemVisualizer<Model.Entity>
    fun createEntityGroupVisualizer(objGroup: Model.EntityGroup): ItemVisualizer<Model.EntityGroup>
    fun createLightBarVisualizer(lightBar: LightBar): ItemVisualizer<LightBar>
    fun createLightRingVisualizer(lightRing: LightRing): ItemVisualizer<LightRing>
    fun createMovingHeadVisualizer(movingHead: MovingHead): ItemVisualizer<MovingHead>
    fun createPolyLineVisualizer(polyLine: PolyLine): ItemVisualizer<PolyLine>
    fun createSurfaceVisualizer(surface: Model.Surface): ItemVisualizer<Model.Surface>
}

class StubEntityAdapter : EntityAdapter {
    override val simulationEnv: SimulationEnv get() = error("Not implemented.")
    override val units: ModelUnit = ModelUnit.default
    override val isEditing: Boolean = false

    override fun createVisualizer(entity: Model.Entity): ItemVisualizer<Model.Entity> = error("Not implemented.")
    override fun createEntityGroupVisualizer(objGroup: Model.EntityGroup): ItemVisualizer<Model.EntityGroup> = error("Not implemented.")
    override fun createLightBarVisualizer(lightBar: LightBar): ItemVisualizer<LightBar> = error("Not implemented.")
    override fun createLightRingVisualizer(lightRing: LightRing): ItemVisualizer<LightRing> = error("Not implemented.")
    override fun createMovingHeadVisualizer(movingHead: MovingHead): ItemVisualizer<MovingHead> = error("Not implemented.")
    override fun createPolyLineVisualizer(polyLine: PolyLine): ItemVisualizer<PolyLine> = error("Not implemented.")
    override fun createSurfaceVisualizer(surface: Model.Surface): ItemVisualizer<Model.Surface> = error("Not implemented.")
}