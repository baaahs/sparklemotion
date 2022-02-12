package baaahs.visualizer

import three.js.BufferGeometry
import three.js.Points
import three.js.PointsMaterial
import three.js.Vector3

class PixelsPreview : Points<BufferGeometry, PointsMaterial>(BufferGeometry(), EntityStyle.pointsMaterial()) {
    fun applyStyle(entityStyle: EntityStyle) {
        entityStyle.applyToPoints(material, EntityStyle.Use.Pixel)
    }

    fun setLocations(pixelLocations: Array<Vector3>) {
        geometry.setFromPoints(pixelLocations)
    }
}