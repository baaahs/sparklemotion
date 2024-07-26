@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

external interface `T$34` {
    var radius: Number
    var widthSegments: Number
    var heightSegments: Number
    var phiStart: Number
    var phiLength: Number
    var thetaStart: Number
    var thetaLength: Number
}

open external class SphereGeometry(radius: Number = definedExternally, widthSegments: Number = definedExternally, heightSegments: Number = definedExternally, phiStart: Number = definedExternally, phiLength: Number = definedExternally, thetaStart: Number = definedExternally, thetaLength: Number = definedExternally) : Geometry {
    override var type: String
    open var parameters: `T$34`
}

open external class SphereBufferGeometry(radius: Number = definedExternally, widthSegments: Number = definedExternally, heightSegments: Number = definedExternally, phiStart: Number = definedExternally, phiLength: Number = definedExternally, thetaStart: Number = definedExternally, thetaLength: Number = definedExternally) : BufferGeometry {
    override var type: String
    open var parameters: `T$34`
}