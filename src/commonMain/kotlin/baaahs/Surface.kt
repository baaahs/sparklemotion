package baaahs

import baaahs.geom.Vector3F

/**
 * Represents a surface whose lighting can be controlled.
 *
 * May or may not be associated with a [Model.Surface].
 */
interface Surface {
    val pixelCount: Int

    fun describe(): String
}

/**
 * A surface which has been associated with a specific [Model.Surface].
 */
class IdentifiedSurface(
    val modelSurface: Model.Surface,
    override val pixelCount: Int,
    /** Each pixel's location in the global 3d model. */
    val pixelLocations: List<Vector3F?>? = emptyList()
) : Surface {
    val name: String = modelSurface.name
    override fun describe(): String = modelSurface.description

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as IdentifiedSurface

        if (modelSurface != other.modelSurface) return false

        return true
    }

    override fun hashCode(): Int {
        return modelSurface.hashCode()
    }


}

/**
 * A surface whose identity isn't known.
 */
class AnonymousSurface(
    val brainId: BrainId,
    override val pixelCount: Int = SparkleMotion.MAX_PIXEL_COUNT
) : Surface {
    override fun describe(): String = "Anonymous surface at $brainId"
    override fun equals(other: Any?): Boolean = other is AnonymousSurface && brainId.equals(other.brainId)
    override fun hashCode(): Int = brainId.hashCode()
}
