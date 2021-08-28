package baaahs.model

import baaahs.geom.Vector3F

interface LinearPixelArray : PixelArray {
    fun calculatePixelLocation(index: Int, count: Int): Vector3F

    override fun calculatePixelLocations(expectedPixelCount: Int): List<Vector3F> {
        return (0 until expectedPixelCount).map { i ->
            calculatePixelLocation(i, expectedPixelCount)
        }
    }
}

interface PlacedPixelArray : PixelArray

interface PixelArray : Model.Entity {
    fun calculatePixelLocations(expectedPixelCount: Int): List<Vector3F>
}