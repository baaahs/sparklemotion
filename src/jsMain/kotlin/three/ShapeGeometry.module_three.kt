@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class ShapeGeometry : Geometry {
    constructor(shapes: Shape, curveSegments: Number = definedExternally)
    constructor(shapes: Array<Shape>, curveSegments: Number = definedExternally)
    override var type: String
    open fun addShapeList(shapes: Array<Shape>, options: Any): ShapeGeometry
    open fun addShape(shape: Shape, options: Any = definedExternally)
}