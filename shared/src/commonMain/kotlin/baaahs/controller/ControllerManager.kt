package baaahs.controller

import baaahs.fixtures.FixtureMapping
import baaahs.fixtures.FixtureOptions
import baaahs.scene.ControllerConfig
import baaahs.scene.MutableControllerConfig
import kotlin.contracts.ExperimentalContracts

/**
 * Instances of `ControllerManager` are responsible for creating controllers
 * from configuration, and discovering controllers.
 *
 * When configuration for this type of controller is found, or changed, or removed,
 * [onChange] is called, and should return a [Controller] if it makes sense to.
 *
 * When a new controller is discovered, the ControllerManager should call [onStateChange].
 * Its [onChange] will then immediately be called.
 */
interface ControllerManager<T : Controller, C: ControllerConfig, S: ControllerState> {
    val controllerType: String

    fun addStateChangeListener(listener: ControllerStateChangeListener<S>)
    fun removeStateChangeListener(listener: ControllerStateChangeListener<S>)
    fun onStateChange(controllerId: ControllerId, changeState: (fromState: S?) -> S?)

    /**
     * Called by the [ControllersManager] when a scene is initially loaded, or when a controller configuration
     * has been edited, as well as when the controller's state changes (as notified via
     * [ControllerStateChangeListener]).
     *
     * Changes in the controller configuration and state are automatically synchronized with the client for
     * display in the UI.
     * 
     * @param controllerId The unique identifier of the controller.
     * @param oldController Previously returned controller, if any.
     * @param config Previous configuration of the controller, if any.
     * @param state Previous state of the controller, if any.
     * @param newConfig New configuration to be applied to the controller.
     * @param newState New state to be applied to the controller.
     * @return If `oldController` is returned, no action is taken.
     *
     * If `oldController` is not null and null is returned, any mapped fixtures are released and the old
     *         controller is disposed of.
     *
     * If `oldController` is null and null is returned, no action is taken.
     *
     * If `oldController` is null and a new controller is returned, the controller becomes active and
     *         any mapped fixtures are bound to it.
     *
     * If `oldController` is not null and a aifferent new controller is returned, the old controller is
     *         released, the new controller becomes active, and any mapped fixtures are moved to the new controller.
     */
    fun onChange(
        controllerId: ControllerId,
        oldController: T?,
        controllerConfig: Change<C?>,
        controllerState: Change<S?>,
        fixtureMappings: Change<List<FixtureMapping>>
    ): T?

    fun start()
    fun reset() {}
    fun stop()

    interface Meta {
        val controllerTypeName: String
        val controllerIcon: String

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

@OptIn(ExperimentalContracts::class)
class Change<T>(val oldValue: T, val newValue: T) {
    val changed: Boolean get() = oldValue != newValue
    val remainedNull: Boolean get() = oldValue == null && newValue == null
    val becameNull: Boolean get() = oldValue != null && newValue == null
    val becameNotNull: Boolean get() = oldValue == null && newValue != null
    val remainedNotNull: Boolean get() = oldValue != null && newValue != null
}

fun interface ControllerStateChangeListener<S : ControllerState> {
    /**
     * Invoked by [ControllerManager] when the state of a controller changes.
     *
     * [ControllerManager.onChange] will immediately be invoked with the affected controller,
     * along with its old state, so any state change handling can be performed.
     *
     * State change is automatically propagated to the client for UI display.
     *
     * @param controllerId The unique identifier of the controller whose state has changed.
     * @param state The new state of the controller.
     */
    fun onStateChange(controllerId: ControllerId, changeState: (fromState: S?) -> S?)
}


abstract class BaseControllerManager<T : Controller, C: ControllerConfig, S: ControllerState>(
    override val controllerType: String
) : ControllerManager<T, C, S> {
    private val listeners: MutableList<ControllerStateChangeListener<S>> = mutableListOf()

    override fun addStateChangeListener(listener: ControllerStateChangeListener<S>) {
        listeners.add(listener)
    }

    override fun removeStateChangeListener(listener: ControllerStateChangeListener<S>) {
        listeners.remove(listener)
    }

    override fun onStateChange(controllerId: ControllerId, changeState: (fromState: S?) -> S?) {
        listeners.forEach { listener -> listener.onStateChange(controllerId, changeState) }
    }
}