package baaahs.sim

import baaahs.model.PixelArray

expect class LightBarSimulation(
    pixelArray: PixelArray,
    simulationEnv: SimulationEnv
) : FixtureSimulation
