@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

external interface `T$36` {
    var width: Number
    var height: Number
    var widthSegments: Number
    var heightSegments: Number
}

open external class PlaneGeometry(width: Number = definedExternally, height: Number = definedExternally, widthSegments: Number = definedExternally, heightSegments: Number = definedExternally) : Geometry {
    override var type: String
    open var parameters: `T$36`
}

open external class PlaneBufferGeometry(width: Number = definedExternally, height: Number = definedExternally, widthSegments: Number = definedExternally, heightSegments: Number = definedExternally) : BufferGeometry {
    override var type: String
    open var parameters: `T$36`
}