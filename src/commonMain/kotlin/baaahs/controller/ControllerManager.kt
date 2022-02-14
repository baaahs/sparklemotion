package baaahs.controller

import baaahs.scene.ControllerConfig

/** A ControllerManager discovers and registers controllers with its [ControllerListener]. */
interface ControllerManager {
    val controllerType: String

    fun addListener(controllerListener: ControllerListener)
    fun removeListener(controllerListener: ControllerListener)
    fun onConfigChange(controllerConfigs: Map<String, ControllerConfig>)
    fun start()
    fun stop()
}

abstract class BaseControllerManager(
    override val controllerType: String
) : ControllerManager {
    private val listeners: MutableList<ControllerListener> = mutableListOf()

    override fun addListener(controllerListener: ControllerListener) {
        listeners.add(controllerListener)
    }

    override fun removeListener(controllerListener: ControllerListener) {
        listeners.remove(controllerListener)
    }

    fun notifyListeners(block: ControllerListener.() -> Unit) {
        listeners.forEach(block)
    }
}