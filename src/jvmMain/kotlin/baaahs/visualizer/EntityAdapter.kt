package baaahs.visualizer

import baaahs.model.*

actual class EntityAdapter : Adapter<Model.Entity> {
    override fun createVisualizer(entity: Model.Entity): ItemVisualizer<Model.Entity> =
        TODO("not implemented")

    actual fun createLightBarVisualizer(lightBar: LightBar): ItemVisualizer<LightBar> =
        TODO("not implemented")

    actual fun createLightRingVisualizer(lightRing: LightRing): ItemVisualizer<LightRing> =
        TODO("not implemented")

    actual fun createMovingHeadVisualizer(movingHead: MovingHead): ItemVisualizer<MovingHead> =
        TODO("not implemented")

    actual fun createObjGroupVisualizer(objGroup: ObjGroup): ItemVisualizer<ObjGroup> =
        TODO("not implemented")

    actual fun createPolyLineVisualizer(polyLine: PolyLine): ItemVisualizer<PolyLine> =
        TODO("not implemented")

    actual fun createSurfaceVisualizer(surface: Model.Surface): ItemVisualizer<Model.Surface> =
        TODO("not implemented")
}