@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class PlaneHelper(plane: Plane, size: Number = definedExternally, hex: Number = definedExternally) : LineSegments<dynamic, dynamic> {
    override var type: String
    open var plane: Plane
    open var size: Number
    override fun updateMatrixWorld(force: Boolean)
}