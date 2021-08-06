package baaahs.sim

import baaahs.model.LightRing

expect class LightRingSimulation(
    lightRing: LightRing,
    simulationEnv: SimulationEnv
) : FixtureSimulation
