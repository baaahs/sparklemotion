package baaahs.model

import baaahs.geom.Vector3F

interface LinearPixelArray : PixelArray {
    /**
     * Calculates the location of a single pixel in the local space of this entity.
     *
     * Transformations indicated by [Model.Entity.position], [Model.Entity.rotation],
     * and [Model.Entity.scale] should *NOT* be applied.
     */
    fun calculatePixelLocalLocation(index: Int, count: Int): Vector3F

    override fun calculatePixelLocalLocations(expectedPixelCount: Int): List<Vector3F> {
        return (0 until expectedPixelCount).map { i ->
            calculatePixelLocalLocation(i, expectedPixelCount)
        }
    }
}

interface PlacedPixelArray : PixelArray

interface PixelArray : Model.Entity {
    /**
     * Calculates the locations of all pixels in the local space of this entity.
     *
     * Transformations indicated by [Model.Entity.position], [Model.Entity.rotation],
     * and [Model.Entity.scale] should *NOT* be applied.
     */
    fun calculatePixelLocalLocations(expectedPixelCount: Int): List<Vector3F>
}