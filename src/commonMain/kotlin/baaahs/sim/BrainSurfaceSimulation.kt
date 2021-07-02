package baaahs.sim

import baaahs.model.Model

expect class BrainSurfaceSimulation(
    surface: Model.Surface,
    simulationEnv: SimulationEnv
) : FixtureSimulation