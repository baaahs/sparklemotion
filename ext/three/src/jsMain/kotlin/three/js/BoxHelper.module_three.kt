@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class BoxHelper : LineSegments<dynamic, dynamic> {
    constructor(obj: Object3D, color: Color = definedExternally)
    constructor(obj: Object3D, color: String = definedExternally)
    constructor(obj: Object3D, color: Number = definedExternally)
    override var type: String
    open fun update(obj: Object3D = definedExternally)
    open fun setFromObject(obj: Object3D): BoxHelper /* this */
}