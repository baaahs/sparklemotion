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
            mappingSession.surfaces.forEach { entityData ->
                val controllerId = entityData.controllerId
                val entityName = entityData.entityName

                try {
                    val modelEntity = model.getEntity(entityName)
                    if (modelEntity == null)
                        logger.warn { "Unknown model entity \"$entityName\"." }

                    val pixelLocations = entityData.pixels?.map { it?.modelPosition }
                        ?.ifEmpty { null }
                    val pixelCount = entityData.pixelCount ?: pixelLocations?.size
                    val transportConfig = when (controllerId.controllerType) {
                        "SACN" -> entityData.channels?.let { SacnTransportConfig(it.start, it.end) }
                        "DMX" -> entityData.channels?.let { DmxTransportConfig(it.start, it.end) }
                        else -> null
                    }

                    val fixtureMapping = FixtureMapping(
                        modelEntity, pixelCount, pixelLocations,
                        entityData.fixtureConfig, transportConfig
                    )
                    add(controllerId, fixtureMapping)
                } catch (e: Exception) {
                    logger.warn(e) { "Skipping $entityName." }
                }
            }
        }

        model.generateFixtureMappings().forEach { (controllerId, fixtureMappings) ->
            fixtureMappings.forEach { fixtureMapping ->
                add(controllerId, fixtureMapping)
            }
        }
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