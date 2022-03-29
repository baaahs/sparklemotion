package baaahs.sim

import baaahs.controller.sim.ControllerSimulator
import baaahs.model.Model
import baaahs.visualizer.EntityAdapter

expect class BrainSurfaceSimulation(
    surface: Model.Surface,
    simulationEnv: SimulationEnv,
    adapter: EntityAdapter,
    controllerSimulator: ControllerSimulator
) : FixtureSimulation