package three.js

open external class ShapePath {
    open val type: String /* "ShapePath" */
    open var subPaths: Array<Path>
    open val currentPath: Path?
    open var color: Color
    open fun moveTo(x: Number, y: Number): ShapePath /* this */
    open fun lineTo(x: Number, y: Number): ShapePath /* this */
    open fun quadraticCurveTo(aCPx: Number, aCPy: Number, aX: Number, aY: Number): ShapePath /* this */
    open fun bezierCurveTo(aCP1x: Number, aCP1y: Number, aCP2x: Number, aCP2y: Number, aX: Number, aY: Number): ShapePath /* this */
    open fun splineThru(pts: Array<Vector2>): ShapePath /* this */
    open fun toShapes(isCCW: Boolean): Array<Shape>
}