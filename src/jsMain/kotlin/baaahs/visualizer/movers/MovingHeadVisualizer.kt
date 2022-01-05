package baaahs.visualizer.movers

import baaahs.geom.Matrix4F
import baaahs.model.Model
import baaahs.model.MovingHead
import baaahs.sim.SimulationEnv
import baaahs.util.Clock
import baaahs.visualizer.EntityVisualizer
import baaahs.visualizer.VizObj

class MovingHeadVisualizer(
    private val movingHead: MovingHead,
    simulationEnv: SimulationEnv,
    private val beam: Beam = Beam.selectFor(movingHead)
) : EntityVisualizer {
    override val entity: Model.Entity get() = movingHead
    override val title: String
        get() = movingHead.name
    override var mapperIsRunning: Boolean = false
    override var selected: Boolean = false // TODO: Show that the device is selected.

    override var transformation: Matrix4F = Matrix4F.identity

    private val clock = simulationEnv[Clock::class]
    private val physicsModel = PhysicsModel(movingHead.adapter, clock)

    override fun addTo(parent: VizObj) {
        beam.addTo(parent)
    }

    internal fun receivedUpdate(buffer: MovingHead.Buffer) {
        beam.update(physicsModel.update(buffer))
    }
}
