package baaahs.visualizer.movers

import baaahs.model.ModelUnit
import baaahs.model.MovingHead
import baaahs.model.MovingHeadAdapter
import three.js.Group
import three.js.Object3D

interface Beam {
    val vizObj: Object3D

    fun update(state: State)

    companion object {
        fun selectFor(movingHeadAdapter: MovingHeadAdapter, units: ModelUnit): Beam {
            return when (movingHeadAdapter.colorModel) {
                MovingHead.ColorModel.ColorWheel -> ColorWheelBeam(movingHeadAdapter, units)
                MovingHead.ColorModel.RGB -> RgbBeam(movingHeadAdapter, units)
                MovingHead.ColorModel.RGBW -> RgbBeam(movingHeadAdapter, units)
            }
        }
    }
}

class ColorWheelBeam(movingHeadAdapter: MovingHeadAdapter, units: ModelUnit) : Beam {
    private val primaryCone = Cone(movingHeadAdapter, units, ColorMode.Primary)
    private val secondaryCone = Cone(movingHeadAdapter, units, ColorMode.Secondary)
    private val cones = Group().also {
        primaryCone.addTo(it)
        secondaryCone.addTo(it)
        it.name = "ColorWheelBeam"
    }

    override val vizObj: Object3D
        get() = cones

    override fun update(state: State) {
        primaryCone.update(state)
        secondaryCone.update(state)
    }
}

class RgbBeam(movingHeadAdapter: MovingHeadAdapter, units: ModelUnit) : Beam {
    private val cone = Cone(movingHeadAdapter, units)
    private val cones = Group().also {
        cone.addTo(it)
        it.name = "RgbBeam"
    }

    override val vizObj: Object3D
        get() = cones

    override fun update(state: State) {
        cone.update(state)
    }
}