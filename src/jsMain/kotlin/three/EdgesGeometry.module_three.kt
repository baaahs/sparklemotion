@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

external interface `T$38` {
    var thresholdAngle: Number
}

open external class EdgesGeometry : BufferGeometry {
    constructor(geometry: BufferGeometry, thresholdAngle: Number = definedExternally)
    constructor(geometry: Geometry, thresholdAngle: Number = definedExternally)
    override var type: String
    open var parameters: `T$38`
}