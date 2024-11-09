package baaahs.controller

import baaahs.PubSub
import baaahs.plugin.Plugins
import baaahs.sm.webapi.Topics
import baaahs.ui.Observable
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

class ControllersPublisher(
    pubSub: PubSub.Server,
    plugins: Plugins
) : ControllerListener {
    private val controllerStatesChannel = pubSub.openChannel(Topics.createControllerStates(plugins), emptyMap()) {}
    private val controllerStates = mutableMapOf<ControllerId, ControllerState>()

    override fun onAdd(controller: Controller) {
        println("ControllersPublisher: onAdd($controller)")
        controllerStates[controller.controllerId] = controller.state
        controllerStatesChannel.onChange(controllerStates)
    }

    override fun onRemove(controller: Controller) {
        controllerStates.remove(controller.controllerId)
        controllerStatesChannel.onChange(controllerStates)
    }

    override fun onError(controller: Controller) {
    }
}

@Serializable
abstract class ControllerState : Observable() {
    abstract val title: String
    abstract val address: String?
    abstract val onlineSince: Instant?
    abstract val firmwareVersion: String?
    abstract val lastErrorMessage: String?
    abstract val lastErrorAt: Instant?

    open fun matches(controllerMatcher: ControllerMatcher): Boolean =
        controllerMatcher.matches(title, address)
}