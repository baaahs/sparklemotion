package baaahs.controller

import baaahs.scene.ControllerConfig

/** A ControllerManager discovers and registers controllers with its [ControllerListener]. */
interface ControllerManager {
    val controllerType: String

    fun start(controllerListener: ControllerListener)
    fun onConfigChange(controllerConfigs: List<ControllerConfig>)
    fun stop()
    fun logStatus()
}