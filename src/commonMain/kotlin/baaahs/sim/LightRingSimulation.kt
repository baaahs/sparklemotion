package baaahs.sim

import baaahs.model.LightRing
import baaahs.visualizer.EntityAdapter

expect class LightRingSimulation(
    lightRing: LightRing,
    simulationEnv: SimulationEnv,
    adapter: EntityAdapter
) : FixtureSimulation
