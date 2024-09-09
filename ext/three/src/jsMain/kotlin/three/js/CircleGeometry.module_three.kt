@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

external interface `T$40` {
    var radius: Number
    var segments: Number
    var thetaStart: Number
    var thetaLength: Number
}

open external class CircleGeometry(radius: Number = definedExternally, segments: Number = definedExternally, thetaStart: Number = definedExternally, thetaLength: Number = definedExternally) : Geometry {
    override var type: String
    open var parameters: `T$40`
}