package baaahs.mapper

import baaahs.model.Model
import baaahs.util.Logger

interface MappingResults {
    fun dataForController(controllerId: ControllerId): FixtureMapping?

    fun dataForEntity(entityName: String): FixtureMapping?

}

data class ControllerId(val controllerType: String, val id: String) {
    fun shortName(): String = "$controllerType:$id"
}

class SessionMappingResults(model: Model, mappingSessions: List<MappingSession>) : MappingResults {
    val controllerData = mutableMapOf<ControllerId, FixtureMapping>()

    init {
        mappingSessions.forEach { mappingSession ->
            mappingSession.surfaces.forEach { surfaceData ->
                val controllerId = surfaceData.controllerId
                val entityName = surfaceData.entityName

                try {
                    val modelEntity = model.findEntity(entityName)
                    val pixelLocations = surfaceData.pixels.map { it?.modelPosition }

                    controllerData[controllerId] = FixtureMapping(modelEntity, pixelLocations)
                } catch (e: Exception) {
                    logger.warn(e) { "Skipping $entityName." }
                }
            }
        }
    }

    override fun dataForController(controllerId: ControllerId): FixtureMapping? =
        controllerData[controllerId]

    override fun dataForEntity(entityName: String): FixtureMapping? {
        return controllerData.values.find { it.entity.name == entityName }
    }

    companion object {
        private val logger = Logger("SessionMappingResults")
    }
}