package baaahs.mapper

import baaahs.BrainId
import baaahs.geom.Vector3F
import baaahs.model.Model
import baaahs.util.Logger

interface MappingResults {
    fun dataForBrain(brainId: BrainId): Info?
    fun dataForSurface(surfaceName: String) : Map<BrainId, Info>?
    fun forEachBrain(f: (Map.Entry<BrainId, Info>) -> Unit)
    fun forEachSurface(f: (Map.Entry<String, MutableMap<BrainId, Info>>) -> Unit)

    class Info(
        val surface: Model.Surface,

        /** Pixel's estimated position within the model. */
        val pixelLocations: List<Vector3F?>?
    )
}

class SessionMappingResults(model: Model, mappingSessions: List<MappingSession>) : MappingResults {
    private val byBrain = mutableMapOf<BrainId, MappingResults.Info>()
    private val bySurface = mutableMapOf<String, MutableMap<BrainId, MappingResults.Info>>()

    init {
        mappingSessions.forEach { mappingSession ->
            mappingSession.surfaces.forEach { surfaceData ->
                val brainId = BrainId(surfaceData.brainId)
                val surfaceName = surfaceData.surfaceName

                try {
                    val modelSurface = model.findSurface(surfaceName)
                    val pixelLocations = surfaceData.pixels.map { it?.modelPosition }
                    val info = MappingResults.Info(modelSurface, pixelLocations)
                    val map = bySurface.getOrPut(surfaceName, { mutableMapOf() })
                    byBrain[brainId] = info
                    map[brainId] = info
                } catch (e: Exception) {
                    logger.warn(e) { "Skipping $surfaceName" }
                }
            }
        }
    }

    override fun dataForBrain(brainId: BrainId): MappingResults.Info? = byBrain[brainId]
    override fun dataForSurface(surfaceName: String) : Map<BrainId, MappingResults.Info>? = bySurface[surfaceName]?.toMap()
    override fun forEachBrain(f: (Map.Entry<BrainId, MappingResults.Info>) -> Unit) = byBrain.forEach(f)
    override fun forEachSurface(f: (Map.Entry<String, MutableMap<BrainId, MappingResults.Info>>) -> Unit) = bySurface.forEach(f)

    companion object {
        private val logger = Logger("SessionMappingResults")
    }
}