@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

external interface `T$37` {
    var points: Array<Vector2>
    var segments: Number
    var phiStart: Number
    var phiLength: Number
}

open external class LatheGeometry(points: Array<Vector2>, segments: Number = definedExternally, phiStart: Number = definedExternally, phiLength: Number = definedExternally) : Geometry {
    override var type: String
    open var parameters: `T$37`
}