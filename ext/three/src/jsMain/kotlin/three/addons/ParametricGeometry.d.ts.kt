@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.BufferGeometry
import three.NormalOrGLBufferAttributes
import three.Vector3

external interface `T$67` {
    var func: (u: Number, v: Number, dest: Vector3) -> Unit
    var slices: Number
    var stacks: Number
}

open external class ParametricGeometry(func: (u: Number, v: Number, target: Vector3) -> Unit = definedExternally, slices: Number = definedExternally, stacks: Number = definedExternally) : BufferGeometry<NormalOrGLBufferAttributes> {
    override var type: String
    open var parameters: `T$67`
}