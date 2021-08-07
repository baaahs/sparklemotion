package baaahs.mapper

import baaahs.dmx.DmxTransportConfig
import baaahs.model.Model
import baaahs.util.Logger
import kotlinx.serialization.Serializable

@Serializable
data class ControllerId(val controllerType: String, val id: String) {
    fun shortName(): String = "$controllerType:$id"
}

class SessionMappingResults(model: Model, mappingSessions: List<MappingSession>) {
    val controllerData = mutableMapOf<ControllerId, MutableList<FixtureMapping>>()

    init {
        mappingSessions.forEach { mappingSession ->
            mappingSession.surfaces.forEach { surfaceData ->
                val controllerId = surfaceData.controllerId
                val entityName = surfaceData.entityName

                try {
                    val modelEntity = model.getEntity(entityName)
                    if (modelEntity == null)
                        logger.warn { "Unknown model entity \"$entityName\"." }

                    val pixelLocations = surfaceData.pixels?.map { it?.modelPosition }
                        ?.ifEmpty { null }
                    val pixelCount = surfaceData.pixelCount ?: pixelLocations?.size
                    val transportConfig = when (controllerId.controllerType) {
                        "SACN" -> surfaceData.channels?.let { SacnTransportConfig(it.start, it.end) }
                        "DMX" -> surfaceData.channels?.let { DmxTransportConfig(it.start, it.end) }
                        else -> null
                    }

                    controllerData.getOrPut(controllerId) { arrayListOf() }
                        .add(
                            FixtureMapping(
                                modelEntity, pixelCount, pixelLocations,
                                transportConfig = transportConfig
                            )
                        )
                } catch (e: Exception) {
                    logger.warn(e) { "Skipping $entityName." }
                }
            }
        }
    }

    fun dataForController(controllerId: ControllerId): List<FixtureMapping> =
        controllerData[controllerId] ?: emptyList()

    companion object {
        private val logger = Logger("SessionMappingResults")
    }
}