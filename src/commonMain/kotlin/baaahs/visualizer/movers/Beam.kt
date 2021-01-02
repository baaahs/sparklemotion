package baaahs.visualizer.movers

import baaahs.model.MovingHead
import baaahs.visualizer.VizScene

interface Beam {
    fun addTo(scene: VizScene)
    fun update(state: State)
}

class ColorWheelBeam(movingHead: MovingHead) : Beam {
    private val primaryCone = Cone(movingHead, ColorMode.Primary)
    private val secondaryCone = Cone(movingHead, ColorMode.Secondary)

    override fun addTo(scene: VizScene) {
        primaryCone.addTo(scene)
        secondaryCone.addTo(scene)
    }

    override fun update(state: State) {
        primaryCone.update(state)
        secondaryCone.update(state)
    }
}

class RgbBeam(movingHead: MovingHead) : Beam {
    private val cone = Cone(movingHead)

    override fun addTo(scene: VizScene) {
        cone.addTo(scene)
    }

    override fun update(state: State) {
        cone.update(state)
    }
}