package baaahs.model

import baaahs.geom.Vector3F

interface LinearPixelArray {
    fun calculatePixelLocation(index: Int, count: Int): Vector3F

    fun calculatePixelLocations(pixelCount: Int): List<Vector3F> {
        return (0 until pixelCount).map { i ->
            calculatePixelLocation(i, pixelCount)
        }
    }
}