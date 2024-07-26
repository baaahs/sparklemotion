@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

external interface `T$31` {
    var radius: Number
    var tube: Number
    var radialSegments: Number
    var tubularSegments: Number
    var arc: Number
}

open external class TorusGeometry(radius: Number = definedExternally, tube: Number = definedExternally, radialSegments: Number = definedExternally, tubularSegments: Number = definedExternally, arc: Number = definedExternally) : Geometry {
    override var type: String
    open var parameters: `T$31`
}