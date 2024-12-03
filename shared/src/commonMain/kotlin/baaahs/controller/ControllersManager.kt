package baaahs.controller

import baaahs.PubSub
import baaahs.fixtures.Fixture
import baaahs.fixtures.FixtureListener
import baaahs.fixtures.FixtureMapping
import baaahs.getBang
import baaahs.mapping.MappingManager
import baaahs.plugin.Plugins
import baaahs.scene.ControllerConfig
import baaahs.scene.OpenControllerConfig
import baaahs.scene.OpenScene
import baaahs.scene.SceneProvider
import baaahs.sm.server.FrameListener
import baaahs.sm.webapi.Topics
import baaahs.ui.addObserver
import baaahs.util.Logger

class ControllersManager(
    private val controllerManagers: List<ControllerManager<out Controller, out ControllerConfig, ControllerState>>,
    private val mappingManager: MappingManager,
    private val sceneProvider: SceneProvider,
    private val fixtureListeners: List<FixtureListener>,
    pubSub: PubSub.Server,
    plugins: Plugins
) : FrameListener {
    private val byType = controllerManagers.associateBy { it.controllerType }
    private var scene: OpenScene? = sceneProvider.openScene
    private val controllerInfos = mutableMapOf<ControllerId, ControllerInfo>()

    private var controllerStates by pubSub.state(
        Topics.createControllerStates(plugins), emptyMap(), allowClientUpdates = false) {}

    init {
        controllerManagers.forEach { controllerManager ->
            controllerManager.addStateChangeListener { controllerId, changeState ->
                gatherChanges {
                    handleStateChange(controllerId, changeState)
                }.updateFixtures()
            }
        }
    }

    fun start() {
        mappingManager.addObserver {
            // When the MappingManager comes online, all fixtures may need a refresh.
            refreshControllerFixtures(controllerInfos.keys)
        }

        sceneProvider.addObserver {
            onSceneChange()
        }

        controllerManagers.forEach { it.start() }
        onSceneChange()
    }

    private fun refreshControllerFixtures(controllerIds: Collection<ControllerId>) {
        val addFixtures = arrayListOf<Fixture>()
        val removeFixtures = arrayListOf<Fixture>()
        for (controllerId in controllerIds) {
            val controllerInfo = controllerInfos[controllerId]
            if (controllerInfo == null) {
                logger.warn { "Found no controllerInfo for $controllerId, skipping." }
                continue
            }

            val controller = controllerInfo.controller
            removeFixtures.addAll(controllerInfo.fixtures)
            if (controllerInfo.release) {
                controllerInfos.remove(controllerId)
                controller?.release()
                continue
            }

            val scene = this.scene
            if (scene != null && mappingManager.dataHasLoaded && controller != null) {
                val newFixtures = scene.resolveFixtures(controller, mappingManager)
                controllerInfo.fixtures = newFixtures
                addFixtures.addAll(newFixtures)
            }
        }

        fixturesChanged(addFixtures, removeFixtures)
    }

    override fun beforeFrame() {
        controllerInfos.values.forEach { it.controller?.beforeFrame() }
    }

    override fun afterFrame() {
        controllerInfos.values.forEach { it.controller?.afterFrame() }
    }

    private fun onSceneChange() {
        this.scene = sceneProvider.openScene

        val incomingConfigs: Map<ControllerId, OpenControllerConfig<ControllerConfig>> =
            (scene?.controllers ?: emptyMap<ControllerId, OpenControllerConfig<*>>())
                    as Map<ControllerId, OpenControllerConfig<ControllerConfig>>
        val priorConfigs = controllerInfos.filterValues { it.controllerConfig != null }

        val incomingFixtureMappings = scene?.fixtureMappings ?: emptyMap()

        val removedConfigs = priorConfigs.keys - incomingConfigs.keys
        val addedConfigs = incomingConfigs.keys - priorConfigs.keys
        val changedConfigs = incomingConfigs.keys.intersect(priorConfigs.keys)
            .filter { controllerId -> priorConfigs[controllerId]!!.controllerState != incomingConfigs[controllerId] }

        gatherChanges {
            (removedConfigs + changedConfigs + addedConfigs).forEach { controllerId ->
                handleConfigChange(
                    controllerId,
                    incomingConfigs[controllerId]?.controllerConfig,
                    incomingFixtureMappings[controllerId] ?: emptyList()
                )
            }
        }.updateFixtures()
    }

    internal inner class ChangeGatherer {
        private val changedControllerIds = mutableSetOf<ControllerId>()
        fun controllerChanged(id: ControllerId): Unit {
            changedControllerIds.add(id)
        }

        fun updateFixtures() {
            refreshControllerFixtures(changedControllerIds)
        }
    }

    private fun gatherChanges(block: ChangeGatherer.() -> Unit): ChangeGatherer =
        ChangeGatherer().apply(block)

    private fun ChangeGatherer.handleConfigChange(
        controllerId: ControllerId,
        controllerConfig: ControllerConfig?,
        fixtureMappings: List<FixtureMapping>
    ) {
        val controllerInfo = controllerInfos[controllerId]
        val oldControllerConfig = controllerInfo?.controllerConfig
        val oldFixtureMappings = controllerInfo?.fixtureMappings ?: emptyList()

        if (controllerConfig == oldControllerConfig && fixtureMappings == oldFixtureMappings)
            return

        sendChange(
            controllerId,
            controllerInfo?.controller,
            controllerConfig,
            controllerInfo?.controllerState,
            fixtureMappings
        )
    }

    private fun <S: ControllerState> ChangeGatherer.handleStateChange(
        controllerId: ControllerId,
        changeState: (S?) -> S?
    ) {
        val controllerInfo = controllerInfos[controllerId]
        val oldState = controllerInfo?.controllerState as S?
        val newState = changeState(oldState)
        if (newState == oldState)
            return

        sendChange(
            controllerId,
            controllerInfo?.controller,
            controllerInfo?.controllerConfig,
            newState,
            controllerInfo?.fixtureMappings
        )
    }

    private fun ChangeGatherer.sendChange(
        controllerId: ControllerId,
        controller: Controller?,
        controllerConfig: ControllerConfig?,
        controllerState: ControllerState?,
        fixtureMappings: List<FixtureMapping>?
    ) {
        val controllerManager: ControllerManager<Controller, ControllerConfig, ControllerState> =
            byType.getBang(controllerId.controllerType, "controller manager")
                    as ControllerManager<Controller, ControllerConfig, ControllerState>

        val controllerInfo = controllerInfos.getOrPut(controllerId) { ControllerInfo() }
        val fromConfig = controllerInfo.controllerConfig
        val fromState = controllerInfo.controllerState
        val fromFixtureMappings = controllerInfo.fixtureMappings
        logger.debug { "${controllerManager.controllerType}: update $controllerId: config=$controllerConfig state=$controllerState" }
        val newController =
            controllerManager.onChange(
                controllerId,
                controller,
                Change(fromConfig, controllerConfig),
                Change(fromState, controllerState),
                Change(fromFixtureMappings, fixtureMappings ?: emptyList()))
        logger.debug { "  --> controller=${if (newController == controller) "SAME" else newController?.let { "new controller"} ?: "NULL" }" }

        controllerInfo.controller = newController

        controllerInfo.controllerConfig = controllerConfig
        controllerInfo.controllerState = controllerState
        controllerInfo.fixtureMappings = fixtureMappings ?: emptyList()

        if (newController == controller) return

        if (newController == null) {
            controllerInfo.release = true
            // Special case: if the controller is going away, we still need a handle
            // to it so we can release it in a moment.
            controllerInfo.controller = controller
        } else {
            if (fromState != controllerState) {
                val newValue = controllerState

                controllerStates = controllerStates.toMutableMap().also { map ->
                    if (newValue != null) {
                        map[controllerId] = newValue
                    } else  {
                        map.remove(controllerId)
                    }
                }
            }
        }

        controllerChanged(controllerId)
    }

    fun reset() {
        controllerManagers.forEach {
            it.reset()
        }
    }

    fun logStatus() {
        val controllerCounts = controllerInfos.keys
            .groupBy { it.controllerType }
            .mapValues { (_, v) -> v.size }
            .entries.sortedBy { (k, _) -> k }

        val total = controllerCounts.sumOf { (_, count) -> count }
        logger.info {
            "$total controllers online (${
                controllerCounts.joinToString(", ") { (type, count) -> "$type=$count" }
            })."
        }
    }

    private fun fixturesChanged(added: List<Fixture> = emptyList(), removed: List<Fixture> = emptyList()) {
        if (added.isNotEmpty() || removed.isNotEmpty())
            fixtureListeners.forEach { it.fixturesChanged(added, removed) }
    }

    private class ControllerInfo(
        var controller: Controller? = null,
        var controllerConfig: ControllerConfig? = null,
        var controllerState: ControllerState? = null,
        var fixtureMappings: List<FixtureMapping> = emptyList(),
        var fixtures: List<Fixture> = emptyList()
    ) {
        var release = false
    }

    companion object {
        private val logger = Logger<ControllersManager>()
    }
}

fun generify(manager: ControllerManager<*, *, *>): ControllerManager<Controller, ControllerConfig, ControllerState> =
    manager as ControllerManager<Controller, ControllerConfig, ControllerState>