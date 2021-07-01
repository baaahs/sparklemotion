package baaahs.sim

import baaahs.model.MovingHead

expect class MovingHeadSimulation(
    movingHead: MovingHead,
    simulationEnv: SimulationEnv
) : FixtureSimulation
