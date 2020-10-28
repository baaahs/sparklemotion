package baaahs.fixtures

import baaahs.BrainId
import baaahs.SparkleMotion
import baaahs.geom.Vector3F
import baaahs.glsl.RandomSurfacePixelStrategy
import baaahs.model.Model
import baaahs.model.ModelInfo

/**
 * Represents a controllable lighting fixture.
 *
 * May or may not be associated with a [Model.Surface].
 */
class Fixture(
    val modelSurface: Model.Surface?,
    val pixelCount: Int,
    /** Each pixel's location in the global 3d model. */
    val pixelLocations: List<Vector3F>,
    val deviceType: DeviceType,
    val name: String = modelSurface?.name ?: "Anonymous fixture"
) {
    fun describe(): String = modelSurface?.description ?: name

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Fixture

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
fun anonymousFixture(
    brainId: BrainId,
    modelInfo: ModelInfo,
    pixelCount: Int = SparkleMotion.MAX_PIXEL_COUNT
) : Fixture {
    return Fixture(
        null,
        pixelCount,
        RandomSurfacePixelStrategy().forUnknownSurface(pixelCount, modelInfo),
        PixelArrayDevice,
        "Anonymous fixture for ${brainId.uuid}"
    )
}
