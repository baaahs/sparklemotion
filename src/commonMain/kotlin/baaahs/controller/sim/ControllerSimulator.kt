package baaahs.controller.sim

import baaahs.controller.ControllerId

interface ControllerSimulator {
    val controllerId: ControllerId

    fun start()
    fun stop()
}