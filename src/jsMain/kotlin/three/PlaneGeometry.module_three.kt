@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
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