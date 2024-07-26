@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class CurvePath<T : Vector> : Curve<T> {
    override var type: String
    open var curves: Array<Curve<T>>
    open var autoClose: Boolean
    open fun add(curve: Curve<T>)
    open fun closePath()
    open fun getPoint(t: Number): T
    open fun getCurveLengths(): Array<Number>
    open fun createPointsGeometry(divisions: Number): Geometry
    open fun createSpacedPointsGeometry(divisions: Number): Geometry
    open fun createGeometry(points: Array<T>): Geometry
}