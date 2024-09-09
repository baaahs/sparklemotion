@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

external interface `T$27` {
    var vertices: Array<Number>
    var indices: Array<Number>
    var radius: Number
    var detail: Number
}

open external class PolyhedronBufferGeometry(vertices: Array<Number>, indices: Array<Number>, radius: Number = definedExternally, detail: Number = definedExternally) : BufferGeometry {
    override var type: String
    open var parameters: `T$27`
}

open external class PolyhedronGeometry(vertices: Array<Number>, indices: Array<Number>, radius: Number = definedExternally, detail: Number = definedExternally) : Geometry {
    override var type: String
    open var parameters: `T$27`
}