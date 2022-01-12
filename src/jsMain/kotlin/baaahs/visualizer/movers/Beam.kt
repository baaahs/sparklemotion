package baaahs.visualizer.movers

import baaahs.model.MovingHead
import baaahs.model.MovingHeadAdapter
import baaahs.visualizer.VizObj
import three.js.Group
import three.js.Object3D

interface Beam {
    val vizObj: Object3D

    fun update(state: State)

    companion object {
        fun selectFor(movingHeadAdapter: MovingHeadAdapter): Beam {
            return when (movingHeadAdapter.colorModel) {
                MovingHead.ColorModel.ColorWheel -> ColorWheelBeam(movingHeadAdapter)
                MovingHead.ColorModel.RGB -> RgbBeam(movingHeadAdapter)
                MovingHead.ColorModel.RGBW -> RgbBeam(movingHeadAdapter)
            }
        }
    }
}

class ColorWheelBeam(movingHeadAdapter: MovingHeadAdapter) : Beam {
    private val primaryCone = Cone(movingHeadAdapter, ColorMode.Primary)
    private val secondaryCone = Cone(movingHeadAdapter, ColorMode.Secondary)
    private val cones = Group().also {
        primaryCone.addTo(VizObj(it))
        secondaryCone.addTo(VizObj(it))
    }

    override val vizObj: Object3D
        get() = cones

    override fun update(state: State) {
        primaryCone.update(state)
        secondaryCone.update(state)
    }
}

class RgbBeam(movingHeadAdapter: MovingHeadAdapter) : Beam {
    private val cone = Cone(movingHeadAdapter)
    private val cones = Group().also {
        cone.addTo(VizObj(it))
    }

    override val vizObj: Object3D
        get() = cones

    override fun update(state: State) {
        cone.update(state)
    }
}