@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

external interface `T$39` {
    var radiusTop: Number
    var radiusBottom: Number
    var height: Number
    var radialSegments: Number
    var heightSegments: Number
    var openEnded: Boolean
    var thetaStart: Number
    var thetaLength: Number
}

open external class CylinderBufferGeometry(radiusTop: Number = definedExternally, radiusBottom: Number = definedExternally, height: Number = definedExternally, radialSegments: Number = definedExternally, heightSegments: Number = definedExternally, openEnded: Boolean = definedExternally, thetaStart: Number = definedExternally, thetaLength: Number = definedExternally) : BufferGeometry {
    override var type: String
    open var parameters: `T$39`
}

open external class CylinderGeometry(radiusTop: Number = definedExternally, radiusBottom: Number = definedExternally, height: Number = definedExternally, radiusSegments: Number = definedExternally, heightSegments: Number = definedExternally, openEnded: Boolean = definedExternally, thetaStart: Number = definedExternally, thetaLength: Number = definedExternally) : Geometry {
    override var type: String
    open var parameters: `T$39`
}