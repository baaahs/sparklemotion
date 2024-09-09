@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

external interface `T$29` {
    var path: Curve<Vector3>
    var tubularSegments: Number
    var radius: Number
    var radialSegments: Number
    var closed: Boolean
}

open external class TubeGeometry(path: Curve<Vector3>, tubularSegments: Number = definedExternally, radius: Number = definedExternally, radiusSegments: Number = definedExternally, closed: Boolean = definedExternally) : Geometry {
    open var parameters: `T$29`
    open var tangents: Array<Vector3>
    open var normals: Array<Vector3>
    open var binormals: Array<Vector3>
}