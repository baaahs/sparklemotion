package baaahs.client

import baaahs.PubSub
import baaahs.controller.ControllerId
import baaahs.controller.ControllerState
import baaahs.fixtures.FixtureInfo
import baaahs.plugin.ClientPlugins
import baaahs.scene.MutableControllerConfig
import baaahs.sm.webapi.Topics
import baaahs.subscribeProperty

class SceneEditorClient(
    private val plugins: ClientPlugins,
    private val pubSub: PubSub.Client
) {
    val facade = Facade()

    private val pubSubListener = { facade.notifyChanged() }.also {
        pubSub.addStateChangeListener(it)
    }

    private val controllerStates by subscribeProperty(pubSub, Topics.createControllerStates(plugins), emptyMap()) { facade.notifyChanged() }
    private val fixtures by subscribeProperty(pubSub, Topics.createFixtures(plugins), emptyList()) { facade.notifyChanged() }

    inner class Facade : baaahs.ui.Facade() {
        val plugins: ClientPlugins
            get() = this@SceneEditorClient.plugins

        val controllerStates: Map<ControllerId, ControllerState>
            get() = this@SceneEditorClient.controllerStates

        val fixtures: List<FixtureInfo>
            get() = this@SceneEditorClient.fixtures

        fun createMutableControllerConfigFor(controllerId: ControllerId): MutableControllerConfig =
            plugins.createMutableControllerConfigFor(controllerId, controllerStates[controllerId])
    }

    fun onClose() {
        pubSub.removeStateChangeListener(pubSubListener)
    }
}
