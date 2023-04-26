package baaahs.visualizer

import baaahs.model.*
import baaahs.sim.SimulationEnv

expect class EntityAdapter(
    simulationEnv: SimulationEnv,
    units: ModelUnit,
    isEditing: Boolean = false
) : Adapter<Model.Entity> {
    fun createEntityGroupVisualizer(objGroup: Model.EntityGroup): ItemVisualizer<Model.EntityGroup>
    fun createLightBarVisualizer(lightBar: LightBar): ItemVisualizer<LightBar>
    fun createLightRingVisualizer(lightRing: LightRing): ItemVisualizer<LightRing>
    fun createMovingHeadVisualizer(movingHead: MovingHead): ItemVisualizer<MovingHead>
    fun createProjectorVisualizer(projector: Projector): ItemVisualizer<Projector>
    fun createPolyLineVisualizer(polyLine: PolyLine): ItemVisualizer<PolyLine>
    fun createSurfaceVisualizer(surface: Model.Surface): ItemVisualizer<Model.Surface>
}