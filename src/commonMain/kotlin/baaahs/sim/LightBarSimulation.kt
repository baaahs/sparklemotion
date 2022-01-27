package baaahs.sim

import baaahs.model.PixelArray
import baaahs.visualizer.EntityAdapter

expect class LightBarSimulation(
    pixelArray: PixelArray,
    simulationEnv: SimulationEnv,
    adapter: EntityAdapter
) : FixtureSimulation
