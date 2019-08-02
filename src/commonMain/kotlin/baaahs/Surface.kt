package baaahs

interface Surface {
    val pixelCount: Int

    fun describe(): String
}

class MappedSurface(val modelSurface: Model.Surface, override val pixelCount: Int) : Surface {
    val name: String = modelSurface.name
    override fun describe(): String = modelSurface.description
}

class UnmappedSurface(val brainId: BrainId) : Surface {
    override val pixelCount = SparkleMotion.PIXEL_COUNT_UNKNOWN

    override fun describe(): String = "Unmapped surface at $brainId"
    override fun equals(other: Any?): Boolean = other is UnmappedSurface && brainId.equals(other.brainId)
    override fun hashCode(): Int = brainId.hashCode()
}
