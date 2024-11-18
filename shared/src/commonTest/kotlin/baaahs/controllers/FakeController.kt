package baaahs.controllers

import baaahs.controller.*
import baaahs.fixtures.*
import baaahs.io.ByteArrayWriter
import baaahs.model.Model
import baaahs.scene.*
import baaahs.scene.mutable.SceneBuilder
import baaahs.ui.View
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
        get() = FakeTransportType

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

data class FakeTransportConfig(
    val startChannel: Int? = null
) : TransportConfig {
    override val transportType: TransportType
        get() = FakeTransportType

    override fun edit(): MutableTransportConfig =
        MutableFakeTransportConfig(this)

    override fun plus(other: TransportConfig?): TransportConfig =
        if (other == null) this
        else plus(other as FakeTransportConfig)

    /** Merges two configs, preferring values from [other]. */
    operator fun plus(other: FakeTransportConfig): FakeTransportConfig = FakeTransportConfig(
        other.startChannel ?: startChannel
    )

    override fun preview(): ConfigPreview = object : ConfigPreview {
        override fun summary(): List<Pair<String, String?>> = listOf(
            "Start Channel" to startChannel?.toString()
        )
    }
}

class MutableFakeTransportConfig(config: FakeTransportConfig) : MutableTransportConfig {
    override val transportType: TransportType
        get() = FakeTransportType

    var startChannel: Int? = config.startChannel

    override fun build(): TransportConfig =
        FakeTransportConfig(startChannel)

    override fun getEditorView(
        editingController: EditingController<*>
    ): View = TODO()

    override fun toSummaryString(): String =
        "$startChannel"
}

object FakeTransportType : TransportType {
    override val id: String
        get() = "FAKE"
    override val title: String
        get() = "Fake"
    override val emptyConfig: TransportConfig
        get() = FakeTransportConfig()
    override val isConfigurable: Boolean
        get() = true
}

class FakeControllerConfig(
    override val title: String = "fake controller",
    override val fixtures: List<FixtureMappingData> = emptyList(),
    override val defaultFixtureOptions: FixtureOptions? = null,
    override val defaultTransportConfig: TransportConfig? = null,
    val anonymousFixtureMapping: FixtureMapping? = null
) : ControllerConfig {
    override val controllerType: String
        get() = FakeControllerManager.controllerTypeName
    override val emptyTransportConfig: TransportConfig
        get() = FakeTransportConfig()

    val controllerId = ControllerId(controllerType, title)

    override fun edit(fixtureMappings: MutableList<MutableFixtureMapping>): MutableControllerConfig =
        MutableFakeControllerConfig(
            title, fixtureMappings, defaultFixtureOptions?.edit(), defaultTransportConfig?.edit()
        )

    override fun createFixturePreview(
        fixtureOptions: FixtureOptions,
        transportConfig: TransportConfig
    ): FixturePreview = TODO("not implemented")
}

class MutableFakeControllerConfig(
    override var title: String,
    override val fixtures: MutableList<MutableFixtureMapping>,
    override var defaultFixtureOptions: MutableFixtureOptions?,
    override var defaultTransportConfig: MutableTransportConfig?,
    val anonymousFixtureMapping: FixtureMapping? = null
) : MutableControllerConfig {
    override val controllerMeta: ControllerManager.Meta = FakeControllerManager
    override val supportedTransportTypes: List<TransportType>
        get() = listOf(FakeTransportType)

    val likelyControllerId: ControllerId =
        ControllerId(FakeControllerManager.controllerTypeName, title)

    override fun build(sceneBuilder: SceneBuilder): FakeControllerConfig =
        FakeControllerConfig(
            title, fixtures.map { it.build(sceneBuilder) },
            defaultFixtureOptions?.build(),
            defaultTransportConfig?.build(),
            anonymousFixtureMapping
        )

    override fun getEditorPanels(editingController: EditingController<*>): List<ControllerEditorPanel<*>> {
        TODO("not implemented")
    }
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

        controllers.addAll(controllerConfigs.values.map { config ->
            with (config.controllerConfig as FakeControllerConfig) {
                FakeController(title, defaultFixtureOptions, defaultTransportConfig, anonymousFixtureMapping)
            }
        })
        if (hasStarted) {
            controllers.forEach { notifyListeners { onAdd(it) } }
        }
    }

    override fun stop() {
        TODO("not implemented")
    }

    companion object : ControllerManager.Meta {
        override val controllerTypeName: String
            get() = "FAKE"

        override fun createMutableControllerConfigFor(
            controllerId: ControllerId?,
            state: ControllerState?
        ): MutableControllerConfig {
            val title = state?.title ?: controllerId?.id ?: "Fake"
            return MutableFakeControllerConfig(title, mutableListOf(), null, null)
        }
    }
}