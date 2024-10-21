package baaahs.visualizer

import three.*

class PixelsPreview {
    val points = Points(BufferGeometry(), EntityStyle.pointsMaterial())

    fun applyStyle(entityStyle: EntityStyle) {
        entityStyle.applyToPoints(points.material, EntityStyle.Use.Pixel)
    }

    fun setLocations(pixelLocations: Array<Vector3>) {
        points.geometry.setFromPoints(pixelLocations)
    }
}