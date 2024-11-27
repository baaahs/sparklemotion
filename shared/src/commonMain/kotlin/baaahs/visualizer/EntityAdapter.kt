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

expect fun createEntityAdapter(simulationEnv: SimulationEnv, modelUnit: ModelUnit): EntityAdapter
