package baaahs.sim

import baaahs.model.PixelArray
import baaahs.visualizer.EntityAdapter

actual abstract class PixelArraySimulation actual constructor(
    pixelArray: PixelArray,
    adapter: EntityAdapter
) : FixtureSimulation