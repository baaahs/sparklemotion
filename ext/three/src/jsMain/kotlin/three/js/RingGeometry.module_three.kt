@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

external interface `T$35` {
    var innerRadius: Number
    var outerRadius: Number
    var thetaSegments: Number
    var phiSegments: Number
    var thetaStart: Number
    var thetaLength: Number
}

open external class RingGeometry(innerRadius: Number = definedExternally, outerRadius: Number = definedExternally, thetaSegments: Number = definedExternally, phiSegments: Number = definedExternally, thetaStart: Number = definedExternally, thetaLength: Number = definedExternally) : Geometry {
    override var type: String
    open var parameters: `T$35`
}