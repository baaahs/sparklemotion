package baaahs.controllers

import baaahs.controller.BaseControllerManager
import baaahs.controller.Controller
import baaahs.controller.ControllerId
import baaahs.controller.ControllerState
import baaahs.dmx.DmxTransportConfig
import baaahs.dmx.DmxTransportType
import baaahs.fixtures.*
import baaahs.io.ByteArrayWriter
import baaahs.model.Model
import baaahs.scene.ControllerConfig
import baaahs.scene.FixtureMappingData
import baaahs.scene.MutableControllerConfig
import baaahs.scene.OpenControllerConfig
import kotlinx.datetime.Instant

class FakeController(
    val name: String,
    override val defaultFixtureOptions: FixtureOptions? = null,
    override val defaultTransportConfig: TransportConfig? = null,
    private val anonymousFixtureMapping: FixtureMapping? = null
) : Controller {
    override val state: ControllerState = object : ControllerState() {
        override val title: String get() = TODO("not implemented")
        override val address: String get() = TODO("not implemented")
        override val onlineSince: Instant? get() = TODO("not implemented")
        override val firmwareVersion: String get() = TODO("not implemented")
        override val lastErrorMessage: String get() = TODO("Not yet implemented")
        override val lastErrorAt: Instant? get() = TODO("Not yet implemented")
    }
    override val transportType: TransportType
        get() = DmxTransportType

    lateinit var transport: FakeTransport
    override val controllerId: ControllerId = ControllerId(type, name)
    override fun createTransport(
        entity: Model.Entity?,
        fixtureConfig: FixtureConfig,
        transportConfig: TransportConfig?
    ): Transport = FakeTransport(transportConfig).also { transport = it }

    override fun getAnonymousFixtureMappings(): List<FixtureMapping> = listOfNotNull(anonymousFixtureMapping)

    inner class FakeTransport(
        override val config: TransportConfig?
    ) : Transport {
        override val name: String get() = this@FakeController.name
        override val controller: Controller
            get() = this@FakeController

        override fun deliverBytes(byteArray: ByteArray) {}

        override fun deliverComponents(
            componentCount: Int,
            bytesPerComponent: Int,
            fn: (componentIndex: Int, buf: ByteArrayWriter) -> Unit
        ) {
        }
    }

    companion object {
        const val type = "FAKE"
    }
}

class FakeControllerConfig(
    override val controllerType: String = "FAKE",
    override val title: String = "fake controller",
    val controllers: List<FakeController> = emptyList(),
    override val fixtures: List<FixtureMappingData> = emptyList(),
    override val defaultFixtureOptions: FixtureOptions? = null,
    override val defaultTransportConfig: TransportConfig? = null
) : ControllerConfig {
    override val emptyTransportConfig: TransportConfig
        get() = DmxTransportConfig()

    override fun edit(): MutableControllerConfig = TODO("not implemented")
    override fun createFixturePreview(
        fixtureOptions: FixtureOptions,
        transportConfig: TransportConfig
    ): FixturePreview = TODO("not implemented")
}

class FakeControllerManager(
    startingControllers: List<FakeController> = emptyList()
) : BaseControllerManager("FAKE") {
    var hasStarted: Boolean = false
    val controllers = startingControllers.toMutableList()

    override fun start() {
        if (hasStarted) error("Already started!")
        hasStarted = true
        controllers.forEach { notifyListeners { onAdd(it) } }
    }

    override fun onConfigChange(controllerConfigs: Map<ControllerId, OpenControllerConfig<*>>) {
        if (hasStarted) {
            controllers.forEach { notifyListeners { onRemove(it) } }
        }

        controllers.clear()

        controllers.addAll(controllerConfigs.values.flatMap { config -> (config.controllerConfig as FakeControllerConfig).controllers })
        if (hasStarted) {
            controllers.forEach { notifyListeners { onAdd(it) } }
        }
    }

    override fun stop() {
        TODO("not implemented")
    }
}