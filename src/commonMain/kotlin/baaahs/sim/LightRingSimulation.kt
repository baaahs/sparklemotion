package baaahs.sim

import baaahs.model.LightRing
import baaahs.visualizer.EntityAdapter

expect class LightRingSimulation(
    lightRing: LightRing,
    adapter: EntityAdapter
) : FixtureSimulation
