package baaahs.sim

import baaahs.model.LightBar
import baaahs.model.PixelArray
import baaahs.model.PolyLine
import baaahs.visualizer.EntityAdapter
import baaahs.visualizer.LightBarVisualizer
import baaahs.visualizer.PixelArrayVisualizer
import baaahs.visualizer.PolyLineVisualizer

actual class LightBarSimulation actual constructor(
    pixelArray: PixelArray,
    private val adapter: EntityAdapter
) : PixelArraySimulation(pixelArray, adapter) {

    override val pixelLocations by lazy { pixelArray.calculatePixelLocalLocations(59) }

    override val itemVisualizer: PixelArrayVisualizer<*> by lazy {
        when (pixelArray) {
            is LightBar -> LightBarVisualizer(pixelArray, adapter, vizPixels)
            is PolyLine -> PolyLineVisualizer(pixelArray, adapter, vizPixels)
            else -> error("unsupported?")
        }
    }


    companion object {
        val pixelVisualizationNormal = three_ext.vector3FacingForward
    }
}