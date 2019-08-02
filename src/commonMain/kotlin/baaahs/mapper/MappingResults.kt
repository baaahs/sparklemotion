package baaahs.mapper

import baaahs.BrainId
import baaahs.Model
import baaahs.geom.Vector2F

interface MappingResults {
    fun dataFor(brainId: BrainId): Info?

    class Info(
        val surface: Model.Surface,
        val pixelLocations: List<Vector2F>?
    )
}

class SessionMappingResults(model: Model<*>, mappingSession: MappingSession?) : MappingResults {
    val brainData = mutableMapOf<BrainId, MappingResults.Info>()

    init {
        if (mappingSession != null) {
            mappingSession.surfaces.forEach { surfaceData ->
                val brainId = BrainId(surfaceData.brainId)
                val surfaceName = surfaceData.surfaceName
                val pixelLocations = surfaceData.pixels.map { pixelData ->
                    pixelData?.screenPosition?.let { (x, y) -> Vector2F(x, y) } ?: Vector2F(0f, 0f)
                }

                val modelSurface = model.findModelSurface(surfaceName)
                brainData[brainId] = MappingResults.Info(modelSurface, pixelLocations)
            }
        }
    }

    override fun dataFor(brainId: BrainId): MappingResults.Info? = brainData[brainId]
}