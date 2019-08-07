package baaahs

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
class IdentifiedSurface(val modelSurface: Model.Surface, override val pixelCount: Int) : Surface {
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
class AnonymousSurface(val brainId: BrainId) : Surface {
    override val pixelCount = SparkleMotion.PIXEL_COUNT_UNKNOWN

    override fun describe(): String = "Unmapped surface at $brainId"
    override fun equals(other: Any?): Boolean = other is AnonymousSurface && brainId.equals(other.brainId)
    override fun hashCode(): Int = brainId.hashCode()
}
