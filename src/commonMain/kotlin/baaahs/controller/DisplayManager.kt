package baaahs.controller

import baaahs.PubSub
import baaahs.device.ProjectorDevice
import baaahs.fixtures.*
import baaahs.gl.Display
import baaahs.gl.Displays
import baaahs.gl.Mode
import baaahs.model.Model
import baaahs.model.Projector
import baaahs.scene.ControllerConfig
import baaahs.scene.MutableControllerConfig
import baaahs.scene.SceneProvider
import baaahs.ui.addObserver
import baaahs.util.Clock
import baaahs.util.Delta
import baaahs.util.Logger
import baaahs.util.Time
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class DisplayManager(
    private val displays: Displays,
    private val sceneProvider: SceneProvider, // TODO: remove
    private val fixtureListeners: List<FixtureListener>, // TODO: remove
    private val clock: Clock,
    private val pubSub: PubSub.Endpoint
) : BaseControllerManager(controllerTypeName) {
    private var currentDisplays = listOf<Display>()
    private var currentProjectors = listOf<Projector>()
    private var currentFixtures = mapOf<Projector, Fixture>()

    private var lastConfig: Map<ControllerId, DisplayControllerConfig> = emptyMap()
    private var controllers: Map<ControllerId, DisplayController> = emptyMap()
    private var discoveredControllers: MutableMap<ControllerId, DisplayController> = hashMapOf()

    override fun start() {
        // Create displays channel.
        displays.Channel(pubSub)

        sceneProvider.addObserver(fireImmediately = true) {
            onSceneChange()
        }

        displays.addObserver(fireImmediately = true) {
            onDisplaysChange()
        }
    }

    override fun onConfigChange(controllerConfigs: Map<ControllerId, ControllerConfig>) {
        handleConfigs(controllerConfigs.filterByType())
    }

    inline fun <reified T : ControllerConfig> Map<ControllerId, ControllerConfig>.filterByType(): Map<ControllerId, T> =
        buildMap {
            this@filterByType.forEach { (k, v) ->
                if (v is T) put(k, v)
            }
        }

    override fun reset() {
        super.reset()
    }

    override fun stop() {
        TODO("not implemented")
    }

    private fun onSceneChange() {
        val scene = sceneProvider.openScene
        val updatedProjectors = buildList {
            scene?.model?.visit { entity ->
                if (entity is Projector) add(entity)
            }
        }

        onChange(updatedProjectors, currentDisplays)
    }

    private fun onDisplaysChange() {
        val updatedDisplays = buildList { addAll(displays.all) }
        onChange(currentProjectors, updatedDisplays)
    }

    private fun onChange(
        updatedProjectors: List<Projector>,
        updatedDisplays: List<Display>
    ) {
        val availableDisplays = updatedDisplays.toMutableList()
        val fixturesToAdd = mutableListOf<Fixture>()
        val fixturesToRemove = mutableListOf<Fixture>()
        val newFixtures = mutableMapOf<Projector, Fixture>()

        updatedProjectors.forEach { projector ->
            val displayName = projector.displayName
            val mode = projector.mode

            val candidates = buildList { addAll(availableDisplays) }.toMutableList()
            if (displayName != null) {
                candidates.removeAll { it.name != displayName }
            }
            if (mode != null) {
                candidates.removeAll { !it.modes.contains(mode) }
            }
            val display = candidates.firstOrNull()
            availableDisplays.remove(display)

            val existingFixture = currentFixtures[projector]
            if (existingFixture != null) {
                val config = existingFixture.fixtureConfig as ProjectorDevice.Config
                if (
                    config.displayName == display?.name
                    && config.width == mode?.width
                    && config.height == mode.height
                ) {
                    newFixtures[projector] = existingFixture
                } else {
                    fixturesToRemove.add(existingFixture)
                    val newFixture = createProjectorFixture(projector, display, mode)
                    newFixtures[projector] = newFixture
                    fixturesToAdd.add(newFixture)
                }
            } else {
                val newFixture = createProjectorFixture(projector, display, mode)
                newFixtures[projector] = newFixture
                fixturesToAdd.add(newFixture)
            }
        }

        currentFixtures.keys.forEach { projector ->
            if (!updatedProjectors.contains(projector)) {
                val fixture = currentFixtures[projector]!!
                fixturesToRemove.add(fixture)
            }
        }

        if (fixturesToAdd.isNotEmpty() || fixturesToRemove.isNotEmpty()) {
            println("fixturesToAdd = ${fixturesToAdd}")
            println("fixturesToRemove = ${fixturesToRemove}")
            fixtureListeners.forEach { it.fixturesChanged(fixturesToAdd, fixturesToRemove) }
        }
        currentFixtures = newFixtures
    }

    private fun handleConfigs(configs: Map<ControllerId, DisplayControllerConfig>) {
        controllers = buildMap {
            Delta.diff(lastConfig, configs, object : Delta.MapChangeListener<ControllerId, DisplayControllerConfig> {
                override fun onAdd(key: ControllerId, value: DisplayControllerConfig) {
                    val display = displays.all.find { it.name == value.displayName }
                        ?: error("Unknown display \"${value.displayName}\".")

                    val mode = value.resolution?.let { Mode(it.first, it.second) }
                        ?: display.modes.firstOrNull()
                        ?: error("No mode specified and no modes available for display \"${value.displayName}\".")

                    val controller = DisplayController(
                        key, display, mode,
                        ProjectorDevice.Config(display.name, mode.width, mode.height),
                        NullTransportConfig,
                        clock.now()
                    )
                    put(controller.controllerId, controller)
                    notifyListeners { onAdd(controller) }
                }

                override fun onRemove(key: ControllerId, value: DisplayControllerConfig) {
                    val oldController = controllers[key]
                    if (oldController == null) {
                        logger.warn { "Unknown controller \"$key\" removed." }
                    } else {
                        oldController.release()
                        notifyListeners { onRemove(oldController) }
                    }
                }
            })
        }
        lastConfig = configs
    }

    private fun createProjectorFixture(projector: Projector, display: Display?, mode: Mode?) =
        Fixture(projector, 1, projector.name, NullTransport, ProjectorDevice,
            ProjectorDevice.Config(display.name, mode))

    @Serializable
    data class State(
        override val title: String,
        override val address: String,
        override val onlineSince: Time?,
        override val firmwareVersion: String? = null,
        override val lastErrorMessage: String? = null,
        override val lastErrorAt: Time? = null
    ) : ControllerState()

    companion object : ControllerManager.Meta {
        override val controllerTypeName = "Display"

        override fun createMutableControllerConfigFor(
            controllerId: ControllerId?,
            state: ControllerState?
        ): MutableControllerConfig {
            TODO("not implemented")
        }

        private val logger = Logger<DisplayManager>()
    }
}

class DisplayController(
    override val controllerId: ControllerId,
    private val display: Display,
    private val mode: Mode,
    override val defaultFixtureOptions: FixtureOptions?,
    override val defaultTransportConfig: TransportConfig,
    private val onlineSince: Time?
) : Controller {
    override val state: ControllerState
        get() = DisplayManager.State(
            "${display.name} (${mode.width}x${mode.height})",
            display.id.toString(), onlineSince)
    override val transportType: TransportType
        get() = NullTransportType

    override fun createTransport(
        entity: Model.Entity?,
        fixtureConfig: FixtureConfig,
        transportConfig: TransportConfig?
    ): Transport {
        TODO("not implemented")
    }

    override fun getAnonymousFixtureMappings(): List<FixtureMapping> {
        TODO("not implemented")
    }

    fun release() {
    }
}

@Serializable @SerialName("Display")
data class DisplayControllerConfig(
    val displayName: String?,
    val resolution: Pair<Int, Int>?
)
