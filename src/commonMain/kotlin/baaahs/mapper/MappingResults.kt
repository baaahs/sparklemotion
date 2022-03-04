package baaahs.mapper

import baaahs.controller.ControllerId
import baaahs.device.PixelArrayDevice
import baaahs.dmx.DmxTransportConfig
import baaahs.fixtures.FixtureMapping
import baaahs.scene.OpenScene
import baaahs.util.Logger

class SessionMappingResults(scene: OpenScene, mappingSessions: List<MappingSession>) {
    val controllerData = mutableMapOf<ControllerId, MutableList<FixtureMapping>>()

    init {
        mappingSessions.forEach { mappingSession ->
            mappingSession.surfaces.forEach { entityData ->
                val controllerId = entityData.controllerId
                val entityName = entityData.entityName

                try {
                    val modelEntity = scene.model.findEntityByNameOrNull(entityName)
                    if (modelEntity == null)
                        logger.warn { "Unknown model entity \"$entityName\"." }

                    val pixelLocations = entityData.pixels?.map { it?.modelPosition }
                        ?.ifEmpty { null }
                    val pixelCount = entityData.pixelCount ?: pixelLocations?.size
                    val transportConfig = when (controllerId.controllerType) {
                        "SACN" -> entityData.channels?.let { DmxTransportConfig(it.start, it.end) }
                        "DMX" -> entityData.channels?.let { DmxTransportConfig(it.start, it.end) }
                        else -> null
                    }

                    val fixtureMapping = FixtureMapping(
                        modelEntity, PixelArrayDevice,
                        entityData.fixtureConfig, transportConfig, pixelLocations
                    )
                    add(controllerId, fixtureMapping)
                } catch (e: Exception) {
                    logger.warn(e) { "Skipping $entityName." }
                }
            }
        }

//        scene.controllers.forEach { (controllerId, controllerConfig) ->
//            controllerConfig.fixtures.forEach { fixtureMapping ->
//                val entity = fixtureMapping.entityId?.let { scene.model.findEntityByNameOrNull(it) }
//
//                if (fixtureMapping.entityId != null && entity == null) {
//                    logger.warn { "No such entity \"${fixtureMapping.entityId} found in model, but there's a fixture mapping for it." }
//                } else {
//                    val pixelArrayDeviceConfig = fixtureMapping.deviceConfig as? PixelArrayDevice.Config
//
//                    add(
//                        controllerId,
//                        FixtureMapping(
//                            entity,
//                            pixelArrayDeviceConfig?.componentCount, // TODO kill this?
//                            null, // TODO kill this?
//                            fixtureMapping.deviceConfig,
//                            fixtureMapping.transportConfig
//                        )
//                    )
//                }
//            }
//        }
    }

    private fun add(controllerId: ControllerId, fixtureMapping: FixtureMapping) {
        controllerData.getOrPut(controllerId) { arrayListOf() }
            .add(fixtureMapping)
    }

    fun dataForController(controllerId: ControllerId): List<FixtureMapping> =
        controllerData[controllerId] ?: emptyList()

    companion object {
        private val logger = Logger("SessionMappingResults")
    }
}