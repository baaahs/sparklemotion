@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

external interface BoxGeometryParameters {
    var width: Number
    var height: Number
    var depth: Number
    var widthSegments: Number
    var heightSegments: Number
    var depthSegments: Number
}

open external class BoxGeometry(width: Number = definedExternally, height: Number = definedExternally, depth: Number = definedExternally, widthSegments: Number = definedExternally, heightSegments: Number = definedExternally, depthSegments: Number = definedExternally) : Geometry {
    override var type: String
    open var parameters: BoxGeometryParameters
}