package baaahs.glshaders

import kotlinx.serialization.Serializable

@Serializable
data class ContentType(val description: String) {
    companion object {
        val UvCoordinateTexture = ContentType("U/V Coordinates Texture")
        val RasterCoordinate = ContentType("Raster Coordinate")
        val UvCoordinate = ContentType("U/V Coordinate")
        val XyCoordinate = ContentType("X/Y Coordinate")
        val Mouse = ContentType("Mouse")
        val XyzCoordinate = ContentType("X/Y/Z Coordinate")

        val Color = ContentType("Color")

        val Time = ContentType("Time")
        val Resolution = ContentType("Resolution")
        val Float = ContentType("Float")
        val Int = ContentType("Integer")
        val Media = ContentType("Media")
        val Unknown = ContentType("Unknown")
    }
}
