package baaahs.sim

import baaahs.model.LightBar

expect class LightBarSimulation(
    lightBar: LightBar,
    simulationEnv: SimulationEnv
) : FixtureSimulation
