@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
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