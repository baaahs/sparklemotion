package baaahs.controller

import baaahs.fixtures.FixtureOptions
import baaahs.scene.ControllerConfig
import baaahs.scene.MutableControllerConfig

/** A ControllerManager discovers and registers controllers with its [ControllerListener]. */
interface ControllerManager {
    val controllerType: String

    fun addListener(controllerListener: ControllerListener)
    fun removeListener(controllerListener: ControllerListener)
    fun onConfigChange(controllerConfigs: Map<ControllerId, ControllerConfig>)
    fun start()
    fun reset() {}
    fun stop()

    interface Meta {
        val controllerTypeName: String

        /**
         * A class of controllers may imply a certain fixture configuration, e.g. colors
         * are automatically gamma corrected.
         */
        val defaultFixtureOptions: FixtureOptions?
            get() = null

        fun createMutableControllerConfigFor(
            controllerId: ControllerId?,
            state: ControllerState?
        ): MutableControllerConfig
    }
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