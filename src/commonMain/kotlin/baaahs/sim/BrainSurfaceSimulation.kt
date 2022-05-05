package baaahs.sim

import baaahs.model.Model
import baaahs.visualizer.EntityAdapter

expect class BrainSurfaceSimulation(
    surface: Model.Surface,
    adapter: EntityAdapter
) : FixtureSimulation