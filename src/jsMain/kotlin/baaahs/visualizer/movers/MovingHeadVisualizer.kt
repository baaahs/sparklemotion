package baaahs.visualizer.movers

import baaahs.model.Model
import baaahs.model.MovingHead
import baaahs.model.MovingHeadAdapter
import baaahs.util.Clock
import baaahs.visualizer.BaseEntityVisualizer
import baaahs.visualizer.EntityAdapter
import baaahs.visualizer.EntityStyle
import baaahs.visualizer.EntityVisualizer
import three.js.*
import three_ext.clear
import three_ext.set

class MovingHeadVisualizer(
    movingHead: MovingHead,
    adapter: EntityAdapter
) : BaseEntityVisualizer<MovingHead>(movingHead) {
    private var beam: Beam = Beam.selectFor(movingHead.adapter)

    private val holder = Group()
    override val obj: Object3D
        get() = holder

    private val clock = adapter.simulationEnv[Clock::class]
    private val physicalModel = PhysicalModel(movingHead.adapter, clock)

    private val moverCan = Mesh(
        CylinderBufferGeometry(),
        MeshMatcapMaterial().apply {
            color.set(0x444444)
        }
    )

    init { update(entity) }

    override fun applyStyle(entityStyle: EntityStyle) {
        // TODO
    }

    override fun isApplicable(newEntity: Model.Entity): MovingHead? =
        newEntity as? MovingHead

    private fun updateCanGeometry(visualizerInfo: MovingHeadAdapter.VisualizerInfo) {
        moverCan.geometry =
            CylinderBufferGeometry(visualizerInfo.canRadius, visualizerInfo.canRadius, visualizerInfo.canLength)

        moverCan.position.y = visualizerInfo.canLength / 2.0 - visualizerInfo.canLengthInFrontOfLight.toDouble()
    }

    override fun update(newEntity: MovingHead, callback: ((EntityVisualizer<*>) -> Unit)?) {
        super.update(newEntity, callback)

        holder.clear()

        updateCanGeometry(entity.adapter.visualizerInfo)
        holder.add(moverCan)

        beam = Beam.selectFor(newEntity.adapter)
        holder.add(beam.vizObj)
    }

    internal fun receivedUpdate(buffer: MovingHead.Buffer) {
        val state = physicalModel.update(buffer)
        beam.update(state)
        moverCan.rotation.set(
            Euler(
                entity.adapter.panRange.scale(state.pan),
                0f,
                entity.adapter.tiltRange.scale(state.tilt)
            )
        )

    }
}
