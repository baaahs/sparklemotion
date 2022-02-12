package baaahs.sim

import baaahs.model.MovingHead
import baaahs.visualizer.EntityAdapter

expect class MovingHeadSimulation(
    movingHead: MovingHead,
    adapter: EntityAdapter
) : FixtureSimulation
