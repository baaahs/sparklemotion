@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

external interface `T$30` {
    var radius: Number
    var tube: Number
    var tubularSegments: Number
    var radialSegments: Number
    var p: Number
    var q: Number
}

open external class TorusKnotGeometry(radius: Number = definedExternally, tube: Number = definedExternally, tubularSegments: Number = definedExternally, radialSegments: Number = definedExternally, p: Number = definedExternally, q: Number = definedExternally) : Geometry {
    override var type: String
    open var parameters: `T$30`
}