package baaahs.visualizer

import baaahs.model.*

expect class EntityAdapter : Adapter<Model.Entity> {
    fun createEntityGroupVisualizer(objGroup: Model.EntityGroup): ItemVisualizer<Model.EntityGroup>
    fun createLightBarVisualizer(lightBar: LightBar): ItemVisualizer<LightBar>
    fun createLightRingVisualizer(lightRing: LightRing): ItemVisualizer<LightRing>
    fun createMovingHeadVisualizer(movingHead: MovingHead): ItemVisualizer<MovingHead>
    fun createPolyLineVisualizer(polyLine: PolyLine): ItemVisualizer<PolyLine>
    fun createSurfaceVisualizer(surface: Model.Surface): ItemVisualizer<Model.Surface>
}