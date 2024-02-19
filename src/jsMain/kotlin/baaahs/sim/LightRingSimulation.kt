package baaahs.sim

import baaahs.model.LightRing
import baaahs.visualizer.EntityAdapter
import baaahs.visualizer.LightRingVisualizer

class LightRingSimulation(
    val lightRing: LightRing,
    adapter: EntityAdapter
) : PixelArraySimulation(lightRing, adapter) {
    // Assuming circumference is in inches, specify 1.5 LEDs per inch, or about 60 per meter.
    private val pixelCount = (lightRing.circumference * 1.5f).toInt()

    override val pixelLocations by lazy { lightRing.calculatePixelLocalLocations(pixelCount) }

    override val itemVisualizer: LightRingVisualizer
            by lazy { LightRingVisualizer(lightRing, adapter, vizPixels) }


    companion object {
        val pixelVisualizationNormal = three_ext.vector3FacingForward
    }
}