@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

external interface `T$26` {
    var func: (u: Number, v: Number, dest: Vector3) -> Unit
    var slices: Number
    var stacks: Number
}

open external class ParametricGeometry(func: (u: Number, v: Number, dest: Vector3) -> Unit, slices: Number, stacks: Number) : Geometry {
    override var type: String
    open var parameters: `T$26`
}