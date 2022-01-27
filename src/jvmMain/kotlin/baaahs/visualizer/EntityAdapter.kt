package baaahs.visualizer

import baaahs.model.*

actual class EntityAdapter : Adapter<Model.Entity> {
    override fun createVisualizer(entity: Model.Entity): EntityVisualizer<Model.Entity> =
        TODO("not implemented")

    actual fun createLightBarVisualizer(lightBar: LightBar): EntityVisualizer<LightBar> =
        TODO("not implemented")

    actual fun createLightRingVisualizer(lightRing: LightRing): EntityVisualizer<LightRing> =
        TODO("not implemented")

    actual fun createMovingHeadVisualizer(movingHead: MovingHead): EntityVisualizer<MovingHead> =
        TODO("not implemented")

    actual fun createObjGroupVisualizer(objGroup: ObjGroup): EntityVisualizer<ObjGroup> =
        TODO("not implemented")

    actual fun createPolyLineVisualizer(polyLine: PolyLine): EntityVisualizer<PolyLine> =
        TODO("not implemented")

    actual fun createSurfaceVisualizer(surface: Model.Surface): EntityVisualizer<Model.Surface> =
        TODO("not implemented")
}