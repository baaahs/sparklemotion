package baaahs.sim

import baaahs.model.PixelArray
import baaahs.visualizer.EntityAdapter

expect abstract class PixelArraySimulation(
    pixelArray: PixelArray,
    adapter: EntityAdapter
) : FixtureSimulation