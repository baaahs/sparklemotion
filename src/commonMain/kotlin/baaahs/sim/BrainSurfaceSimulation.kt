package baaahs.sim

import baaahs.model.Model
import baaahs.visualizer.EntityAdapter

expect class BrainSurfaceSimulation(
    surface: Model.Surface,
    simulationEnv: SimulationEnv,
    adapter: EntityAdapter
) : FixtureSimulation