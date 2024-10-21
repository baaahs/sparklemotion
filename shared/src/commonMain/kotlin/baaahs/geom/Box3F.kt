package baaahs.geom

data class Box3F(
    val min: Vector3F,
    val max: Vector3F
) {
    fun size(): Vector3F = max - min

    fun withPadding(padding: Float = .05f): Box3F {
        val borderSize = size().length() * padding
        val border = Vector3F(borderSize, borderSize, borderSize).abs()
        return Box3F(min - border, max + border)
    }
}