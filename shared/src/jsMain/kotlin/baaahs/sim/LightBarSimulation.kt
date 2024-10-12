package baaahs.sim

import baaahs.model.LightBar
import baaahs.model.PixelArray
import baaahs.model.PolyLine
import baaahs.visualizer.EntityAdapter
import baaahs.visualizer.entity.ItemVisualizer
import baaahs.visualizer.entity.LightBarVisualizer
import baaahs.visualizer.entity.PolyLineVisualizer

class LightBarSimulation(
    pixelArray: PixelArray,
    private val adapter: EntityAdapter
) : PixelArraySimulation(pixelArray, adapter) {

    override val pixelLocations by lazy { pixelArray.calculatePixelLocalLocations(59) }

    override val itemVisualizer: ItemVisualizer<*> by lazy {
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