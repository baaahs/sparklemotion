package baaahs.controller

import baaahs.fixtures.Fixture
import baaahs.fixtures.FixtureConfig
import baaahs.fixtures.FixtureListener
import baaahs.fixtures.Transport
import baaahs.geom.Vector3F
import baaahs.mapper.ControllerId
import baaahs.mapper.FixtureMapping
import baaahs.model.Model
import baaahs.ui.IObservable
import baaahs.ui.addObserver

interface ControllerListener {
    fun onChange(added: Collection<Controller>, removed: Collection<Controller>)
}

class ControllersManager(
    private val controllerManagers: List<ControllerManager>,
    private val mappingManager: MappingManager,
    private val modelManager: ModelManager,
    private val fixtureListener: FixtureListener
) {
    private var managersStarted = false

    private val controllerListener = object : ControllerListener {
        override fun onChange(added: Collection<Controller>, removed: Collection<Controller>) {
            val addedFixtures = mutableListOf<Fixture>()
            added.forEach { controller ->
                val mappings = mappingManager.findMappings(controller.controllerId)
                    .ifEmpty { controller.getAnonymousFixtureMappings() }

                mappings.forEach { mapping ->
                    val defaultMapping = controller.fixtureMapping

                    val pixelCount = mapping.pixelCount
                        ?: defaultMapping?.pixelCount
                        ?: 0

                    val pixelLocations = mapping.pixelLocations
                        ?.map { it ?: Vector3F(0f, 0f, 0f) }
                        ?: emptyList()

                    val modelEntity = mapping.entity

                    val deviceConfig = mapping.fixtureConfig
                        ?: modelEntity?.deviceType?.defaultConfig
                        ?: defaultMapping?.fixtureConfig
                        ?: error("huh? no device config")

                    val deviceOffset = mapping.deviceOffset
                        ?: defaultMapping?.deviceOffset
                        ?: 0

                    addedFixtures.add(
                        Fixture(
                            modelEntity, pixelCount, pixelLocations,
                            deviceConfig.deviceType,
                            controller.controllerId.shortName(),
                            controller.createTransport(modelEntity, deviceConfig, deviceOffset, pixelCount)
                        )
                    )
                }
            }

            if (addedFixtures.isNotEmpty()) {
                fixtureListener.fixturesChanged(addedFixtures, emptyList())
            }
        }
    }

    fun start() {
        mappingManager.addObserver { onMappingChange() }

        maybeStartManagers()
    }

    private fun maybeStartManagers() {
        if (mappingManager.dataHasLoaded && !managersStarted) {
            controllerManagers.forEach { it.start(controllerListener) }
            managersStarted = true
        }
    }

    private fun onMappingChange() {
        maybeStartManagers()
    }
}

interface ControllerManager {
    fun start(controllerListener: ControllerListener)
    fun stop()
}

interface Controller {
    val controllerId: ControllerId
    val fixtureMapping: FixtureMapping?

    fun createTransport(entity: Model.Entity?, fixtureConfig: FixtureConfig, deviceOffset: Int, pixelCount: Int): Transport
    fun getAnonymousFixtureMappings(): List<FixtureMapping>
}

interface MappingManager : IObservable {
    val dataHasLoaded: Boolean

    fun findMappings(controllerId: ControllerId): List<FixtureMapping>
}

interface ModelManager : IObservable {
    fun findEntity(name: String): Model.Entity?
}