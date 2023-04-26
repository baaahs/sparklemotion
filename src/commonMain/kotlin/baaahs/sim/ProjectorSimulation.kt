package baaahs.sim

import baaahs.model.Projector
import baaahs.visualizer.EntityAdapter

expect class ProjectorSimulation(
    projector: Projector,
    adapter: EntityAdapter
) : FixtureSimulation