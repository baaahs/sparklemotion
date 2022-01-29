package baaahs.client

import baaahs.PubSub
import baaahs.controller.SacnDevice
import baaahs.dmx.DmxInfo
import baaahs.fixtures.FixtureInfo
import baaahs.plugin.ClientPlugins
import baaahs.sm.brain.BrainInfo
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

    private val brains by subscribeProperty(pubSub, Topics.brains, emptyMap()) { facade.notifyChanged() }
    private val dmxDevices by subscribeProperty(pubSub, Topics.dmxDevices, emptyMap()) { facade.notifyChanged() }
    private val sacnDevices by subscribeProperty(pubSub, Topics.sacnDevices, emptyMap()) { facade.notifyChanged() }
    private val fixtures by subscribeProperty(pubSub, Topics.createFixtures(plugins), emptyList()) { facade.notifyChanged() }

    inner class Facade : baaahs.ui.Facade() {
        val plugins: ClientPlugins
            get() = this@SceneEditorClient.plugins

        val brains: Map<String, BrainInfo>
            get() = this@SceneEditorClient.brains

        val dmxDevices: Map<String, DmxInfo>
            get() = this@SceneEditorClient.dmxDevices

        val sacnDevices: Map<String, SacnDevice>
            get() = this@SceneEditorClient.sacnDevices

        val fixtures: List<FixtureInfo>
            get() = this@SceneEditorClient.fixtures
    }

    fun onClose() {
        pubSub.removeStateChangeListener(pubSubListener)
    }
}
