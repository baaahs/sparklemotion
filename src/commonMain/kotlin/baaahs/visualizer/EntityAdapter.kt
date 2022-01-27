package baaahs.visualizer

import baaahs.model.*

expect class EntityAdapter : Adapter<Model.Entity> {
    fun createLightBarVisualizer(lightBar: LightBar): EntityVisualizer<LightBar>
    fun createLightRingVisualizer(lightRing: LightRing): EntityVisualizer<LightRing>
    fun createMovingHeadVisualizer(movingHead: MovingHead): EntityVisualizer<MovingHead>
    fun createObjGroupVisualizer(objGroup: ObjGroup): EntityVisualizer<ObjGroup>
    fun createPolyLineVisualizer(polyLine: PolyLine): EntityVisualizer<PolyLine>
    fun createSurfaceVisualizer(surface: Model.Surface): EntityVisualizer<Model.Surface>
}