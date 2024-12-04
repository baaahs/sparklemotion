package baaahs.visualizer

import baaahs.model.ModelUnit
import three.BufferGeometry
import three.Points
import three.Vector3

class PixelsPreview {
    val points = Points(BufferGeometry(), EntityStyle.pointsMaterial())

    fun applyStyle(entityStyle: EntityStyle, units: ModelUnit) {
        entityStyle.applyToPoints(points.material, EntityStyle.Use.Pixel)
        points.material.size = units.fromCm(2)
        points.material.opacity = .8
        points.material.transparent = true
    }

    fun setLocations(pixelLocations: Array<Vector3>) {
        points.geometry.setFromPoints(pixelLocations)
    }
}