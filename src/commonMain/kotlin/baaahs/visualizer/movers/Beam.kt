package baaahs.visualizer.movers

import baaahs.model.MovingHead
import baaahs.visualizer.VizObj

interface Beam {
    fun addTo(scene: VizObj)
    fun update(state: State)

    companion object {
        fun selectFor(movingHead: MovingHead): Beam {
            return when (movingHead.adapter.colorModel) {
                MovingHead.ColorModel.ColorWheel -> ColorWheelBeam(movingHead)
                MovingHead.ColorModel.RGB -> RgbBeam(movingHead)
                MovingHead.ColorModel.RGBW -> RgbBeam(movingHead)
            }
        }
    }
}

class ColorWheelBeam(movingHead: MovingHead) : Beam {
    private val primaryCone = Cone(movingHead, ColorMode.Primary)
    private val secondaryCone = Cone(movingHead, ColorMode.Secondary)

    override fun addTo(scene: VizObj) {
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

    override fun addTo(scene: VizObj) {
        cone.addTo(scene)
    }

    override fun update(state: State) {
        cone.update(state)
    }
}