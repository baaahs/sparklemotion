package baaahs.visualizer.movers

import baaahs.model.Model
import baaahs.model.MovingHead
import baaahs.sim.SimulationEnv
import baaahs.util.Clock
import baaahs.visualizer.BaseEntityVisualizer
import baaahs.visualizer.EntityStyle
import baaahs.visualizer.EntityVisualizer
import three.js.Group
import three.js.Object3D
import three_ext.clear

class MovingHeadVisualizer(
    movingHead: MovingHead,
    simulationEnv: SimulationEnv,
) : BaseEntityVisualizer<MovingHead>(movingHead) {
    private var beam: Beam = Beam.selectFor(movingHead.adapter)

    private val holder = Group()
    override val obj: Object3D
        get() = holder

    private val clock = simulationEnv[Clock::class]
    private val physicalModel = PhysicalModel(movingHead.adapter, clock)

    init { update(entity) }

    override fun applyStyle(entityStyle: EntityStyle) {
        // TODO
    }

    override fun isApplicable(newEntity: Model.Entity): MovingHead? =
        newEntity as? MovingHead

    override fun update(newEntity: MovingHead, callback: ((EntityVisualizer<*>) -> Unit)?) {
        super.update(newEntity, callback)

        holder.clear()
        beam = Beam.selectFor(newEntity.adapter)
        holder.add(beam.vizObj)
    }

    internal fun receivedUpdate(buffer: MovingHead.Buffer) {
        beam.update(physicalModel.update(buffer))
    }
}
