package baaahs.visualizer

import baaahs.model.ModelUnit
import baaahs.sim.SimulationEnv

actual fun createEntityAdapter(simulationEnv: SimulationEnv, modelUnit: ModelUnit): EntityAdapter =
    JsEntityAdapter(simulationEnv, modelUnit)