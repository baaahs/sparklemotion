package baaahs.visualizer.movers

import baaahs.model.MovingHead
import baaahs.sim.MovingHeadVisualizerI
import baaahs.util.Clock
import baaahs.visualizer.BaseEntityVisualizer
import baaahs.visualizer.EntityAdapter
import baaahs.visualizer.EntityStyle
import three.js.Group
import three.js.Object3D
import three_ext.clear

class MovingHeadVisualizer(
    movingHead: MovingHead,
    adapter: EntityAdapter
) : BaseEntityVisualizer<MovingHead>(movingHead), MovingHeadVisualizerI {
    private val holder = Group()
    override val obj: Object3D
        get() = holder

    private val clock = adapter.simulationEnv[Clock::class]
    private val physicalModel = PhysicalModel(movingHead.adapter, clock)

    private val sharpyVisualizer = SharpyVisualizer(movingHead.adapter)

    init {
        update(item)
    }

    override fun applyStyle(entityStyle: EntityStyle) {
        sharpyVisualizer.applyStyle(entityStyle)
    }

    override fun isApplicable(newItem: Any): MovingHead? =
        newItem as? MovingHead

    override fun update(newItem: MovingHead) {
        super.update(newItem)

        holder.clear()

        sharpyVisualizer.updateGeometry(item.adapter.visualizerInfo)
        holder.add(sharpyVisualizer.group)
    }

    override fun receivedUpdate(buffer: MovingHead.Buffer) {
        val state = physicalModel.update(buffer)

        sharpyVisualizer.update(state)
    }
}
