@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class ShapePath {
    open var type: String
    open var color: Color
    open var subPaths: Array<Any>
    open var currentPath: Any
    open fun moveTo(x: Number, y: Number): ShapePath /* this */
    open fun lineTo(x: Number, y: Number): ShapePath /* this */
    open fun quadraticCurveTo(aCPx: Number, aCPy: Number, aX: Number, aY: Number): ShapePath /* this */
    open fun bezierCurveTo(aCP1x: Number, aCP1y: Number, aCP2x: Number, aCP2y: Number, aX: Number, aY: Number): ShapePath /* this */
    open fun splineThru(pts: Array<Vector2>): ShapePath /* this */
    open fun toShapes(isCCW: Boolean, noHoles: Boolean = definedExternally): Array<Shape>
}