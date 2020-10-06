package baaahs.fixtures

import baaahs.BrainId
import baaahs.SparkleMotion
import baaahs.geom.Vector3F
import baaahs.model.Model

/**
 * Represents a controllable lighting fixture.
 *
 * May or may not be associated with a [Model.Surface].
 */
interface Fixture {
    val pixelCount: Int
    val pixelLocations: List<Vector3F?>?

    fun describe(): String
}

/**
 * A fixture which has been associated with a specific [Model.Surface].
 */
class IdentifiedFixture(
    val modelSurface: Model.Surface,
    override val pixelCount: Int,
    /** Each pixel's location in the global 3d model. */
    override val pixelLocations: List<Vector3F?>? = emptyList()
) : Fixture {
    val name: String = modelSurface.name
    override fun describe(): String = modelSurface.description

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as IdentifiedFixture

        if (modelSurface != other.modelSurface) return false

        return true
    }

    override fun hashCode(): Int {
        return modelSurface.hashCode()
    }


}

/**
 * A fixture whose identity isn't known.
 */
class AnonymousFixture(
    val brainId: BrainId,
    override val pixelCount: Int = SparkleMotion.MAX_PIXEL_COUNT
) : Fixture {
    override val pixelLocations: List<Vector3F?>? get() = null
    override fun describe(): String = "Anonymous fixture at $brainId"
    override fun equals(other: Any?): Boolean = other is AnonymousFixture && brainId.equals(other.brainId)
    override fun hashCode(): Int = brainId.hashCode()
}
