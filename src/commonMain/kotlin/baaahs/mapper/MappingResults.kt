package baaahs.mapper

import baaahs.BrainId
import baaahs.geom.Vector3F
import baaahs.model.Model
import baaahs.util.Logger

interface MappingResults {
    fun dataFor(brainId: BrainId): Info?

    fun dataFor(surfaceName: String): Info?

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

                try {
                    val modelSurface = model.findModelSurface(surfaceName)
                    val pixelLocations = surfaceData.pixels.map { it?.modelPosition }

                    brainData[brainId] = MappingResults.Info(modelSurface, pixelLocations)
                } catch (e: Exception) {
                    logger.warn(e) { "Skipping $surfaceName" }
                }
            }
        }
    }

    override fun dataFor(brainId: BrainId): MappingResults.Info? = brainData[brainId]

    override fun dataFor(surfaceName: String): MappingResults.Info? {
        return brainData.values.find { it.surface.name == surfaceName }
    }

    companion object {
        private val logger = Logger("SessionMappingResults")
    }
}