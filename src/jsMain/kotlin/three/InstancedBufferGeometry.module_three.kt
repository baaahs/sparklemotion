@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

external interface `T$47` {
    var start: Number
    var count: Number
    var instances: Number
}

open external class InstancedBufferGeometry : BufferGeometry {
    override var type: String
    override var groups: Array<`T$18`>
    open var instanceCount: Number
    override fun addGroup(start: Number, count: Number, instances: Number)
}