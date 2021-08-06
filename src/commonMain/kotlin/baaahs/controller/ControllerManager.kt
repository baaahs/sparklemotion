package baaahs.controller

import baaahs.scene.ControllerConfig

interface ControllerManager {
    val controllerType: String

    fun start(controllerListener: ControllerListener)
    fun onConfigChange(controllerConfigs: List<ControllerConfig>)
    fun stop()
    fun logStatus()
}