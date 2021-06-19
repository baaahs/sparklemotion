package baaahs.mapper

import baaahs.geom.Vector3F
import baaahs.model.Model
import baaahs.util.Logger

interface MappingResults {
    fun dataForController(controllerId: ControllerId): Info?

    fun dataForEntity(entityName: String): Info?

    class Info(
        val entity: Model.Entity,

        /** Pixel's estimated position within the model. */
        val pixelLocations: List<Vector3F?>?
    )
}

data class ControllerId(val controllerType: String, val id: String) {
    fun shortName(): String = "$controllerType:$id"
}

class SessionMappingResults(model: Model, mappingSessions: List<MappingSession>) : MappingResults {
    val controllerData = mutableMapOf<ControllerId, MappingResults.Info>()

    init {
        mappingSessions.forEach { mappingSession ->
            mappingSession.surfaces.forEach { surfaceData ->
                val controllerId = surfaceData.controllerId
                val entityName = surfaceData.entityName

                try {
                    val modelEntity = model.findEntity(entityName)
                    val pixelLocations = surfaceData.pixels.map { it?.modelPosition }

                    controllerData[controllerId] = MappingResults.Info(modelEntity, pixelLocations)
                } catch (e: Exception) {
                    logger.warn(e) { "Skipping $entityName." }
                }
            }
        }
    }

    override fun dataForController(controllerId: ControllerId): MappingResults.Info? =
        controllerData[controllerId]

    override fun dataForEntity(entityName: String): MappingResults.Info? {
        return controllerData.values.find { it.entity.name == entityName }
    }

    companion object {
        private val logger = Logger("SessionMappingResults")
    }
}