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
    override val transportType: TransportType
        get() = FakeTransportType

    lateinit var transport: FakeTransport
    override val controllerId: ControllerId = ControllerId(type, name)
    var released = false

    override fun createFixtureResolver(): FixtureResolver = object : FixtureResolver {
        override fun createTransport(
            entity: Model.Entity?,
            fixtureConfig: FixtureConfig,
            transportConfig: TransportConfig?
        ): Transport = FakeTransport(transportConfig).also { transport = it }
    }

    override fun release() {
        released = true
    }

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

    class FakeControllerState : ControllerState() {
        override val title: String get() = TODO("not implemented")
        override val address: String get() = TODO("not implemented")
        override val onlineSince: Instant? get() = TODO("not implemented")
        override val firmwareVersion: String get() = TODO("not implemented")
        override val lastErrorMessage: String get() = TODO("Not yet implemented")
        override val lastErrorAt: Instant? get() = TODO("Not yet implemented")
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
        override fun summary(): List<ConfigPreviewNugget> = listOf(
            ConfigPreviewNugget("Start Channel", startChannel?.toString())
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
    override val defaultFixtureOptions: FixtureOptions? = null,
    override val defaultTransportConfig: TransportConfig? = null,
    val anonymousFixtureMapping: FixtureMapping? = null
) : ControllerConfig {
    override val controllerType: String
        get() = FakeControllerManager.controllerTypeName
    override val emptyTransportConfig: TransportConfig
        get() = FakeTransportConfig()

    val controllerId = ControllerId(controllerType, title)

    override fun edit(): MutableControllerConfig =
        MutableFakeControllerConfig(
            title, defaultFixtureOptions?.edit(), defaultTransportConfig?.edit()
        )

    override fun createPreviewBuilder(): PreviewBuilder = TODO("not implemented")
}

class MutableFakeControllerConfig(
    override var title: String,
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
            title,
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
) : BaseControllerManager<FakeController, FakeControllerConfig, ControllerState>("FAKE") {
    var hasStarted: Boolean = false
    val controllers = startingControllers.toMutableList()

    override fun start() {
        if (hasStarted) error("Already started!")
        hasStarted = true
    }

    override fun onChange(
        controllerId: ControllerId,
        oldController: FakeController?,
        controllerConfig: Change<FakeControllerConfig?>,
        controllerState: Change<ControllerState?>,
        fixtureMappings: Change<List<FixtureMapping>>
    ): FakeController? {
        if (!controllerConfig.changed) return oldController

        oldController?.let { controllers.remove(it) }

        return controllerConfig.newValue?.let {
            with(it) {
                FakeController(title, defaultFixtureOptions, defaultTransportConfig, anonymousFixtureMapping)
                    .also { controllers.add(it) }
            }
        }
    }

    override fun stop() {
        TODO("not implemented")
    }

    companion object : ControllerManager.Meta {
        override val controllerTypeName: String
            get() = "FAKE"
        override val controllerIcon: String
            get() = TODO("not implemented")

        override fun createMutableControllerConfigFor(
            controllerId: ControllerId?,
            state: ControllerState?
        ): MutableControllerConfig {
            val title = state?.title ?: controllerId?.id ?: "Fake"
            return MutableFakeControllerConfig(title, null, null)
        }
    }
}