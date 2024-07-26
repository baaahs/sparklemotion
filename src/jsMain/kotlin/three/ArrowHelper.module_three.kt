@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class ArrowHelper(dir: Vector3, origin: Vector3 = definedExternally, length: Number = definedExternally, color: Number = definedExternally, headLength: Number = definedExternally, headWidth: Number = definedExternally) : Object3D {
    override var type: String
    open var line: Line<dynamic, dynamic>
    open var cone: Mesh<dynamic, dynamic>
    open fun setDirection(dir: Vector3)
    open fun setLength(length: Number, headLength: Number = definedExternally, headWidth: Number = definedExternally)
    open fun setColor(color: Color)
    open fun setColor(color: String)
    open fun setColor(color: Number)
}