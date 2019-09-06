package baaahs.mapper

import baaahs.BrainId
import baaahs.Model
import baaahs.geom.Vector3F

interface MappingResults {
    fun dataFor(brainId: BrainId): Info?

    class Info(
        val surface: Model.Surface,

        /** Pixel's estimated position within the model. */
        val pixelLocations: List<Vector3F?>?
    )
}

class SessionMappingResults(model: Model<*>, mappingSessions: List<MappingSession>) : MappingResults {
    val brainData = mutableMapOf<BrainId, MappingResults.Info>()

    init {
        mappingSessions.forEach { mappingSession ->
            mappingSession.surfaces.forEach { surfaceData ->
                val brainId = BrainId(surfaceData.brainId)
                val surfaceName = surfaceData.surfaceName
                val modelSurface = model.findModelSurface(surfaceName)
                val pixelLocations = surfaceData.pixels.map { it?.modelPosition }

                brainData[brainId] = MappingResults.Info(modelSurface, pixelLocations)
            }
        }
    }

    override fun dataFor(brainId: BrainId): MappingResults.Info? = brainData[brainId]
}