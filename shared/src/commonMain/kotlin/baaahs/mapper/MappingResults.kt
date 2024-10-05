package baaahs.mapper

import baaahs.controller.ControllerId
import baaahs.device.PixelArrayDevice
import baaahs.fixtures.FixtureMapping
import baaahs.scene.OpenScene
import baaahs.util.Logger

class SessionMappingResults(scene: OpenScene, mappingSessions: List<MappingSession>) {
    val controllerData: Map<ControllerId, List<FixtureMapping>>

    init {
        val controllerData = mutableMapOf<ControllerId, MutableList<FixtureMapping>>()

        fun add(controllerId: ControllerId, fixtureMapping: FixtureMapping) {
            controllerData.getOrPut(controllerId) { arrayListOf() }
                .add(fixtureMapping)
        }

        mappingSessions.forEach { mappingSession ->
            mappingSession.surfaces.forEach { mappingData ->
                val controllerId = mappingData.controllerId
                val entityName = mappingData.entityName

                try {
                    val modelEntity = scene.model.findEntityByNameOrNull(entityName)
                    if (modelEntity == null)
                        logger.warn { "Unknown model entity \"$entityName\"." }

                    val pixelLocations = mappingData.pixels?.map { it?.modelPosition }
                        ?.ifEmpty { null }
                    val pixelCount = mappingData.pixelCount ?: pixelLocations?.size

                    val fixtureOptions = PixelArrayDevice.Options(
                        pixelCount,
                        pixelLocations = pixelLocations,
                        pixelFormat = mappingData.pixelFormat
                    )

                    val transportConfig = when (controllerId.controllerType) {
                        "SACN", "DMX" -> mappingData.channels
                        else -> null
                    }

                    val fixtureMapping = FixtureMapping(modelEntity, fixtureOptions, transportConfig)
                    add(controllerId, fixtureMapping)
                } catch (e: Exception) {
                    logger.warn(e) { "Skipping $entityName." }
                }
            }
        }
        this.controllerData = controllerData

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

    fun dataForController(controllerId: ControllerId): List<FixtureMapping> =
        controllerData[controllerId] ?: emptyList()

    companion object {
        private val logger = Logger("SessionMappingResults")
    }
}