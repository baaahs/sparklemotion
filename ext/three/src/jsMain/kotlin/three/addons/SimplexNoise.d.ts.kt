@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

open external class SimplexNoise(r: Any? = definedExternally) {
    open fun dot(g: Array<Number>, x: Number, y: Number): Number
    open fun dot3(g: Array<Number>, x: Number, y: Number, z: Number): Number
    open fun dot4(g: Array<Number>, x: Number, y: Number, z: Number, w: Number): Number
    open fun noise(xin: Number, yin: Number): Number
    open fun noise3d(xin: Number, yin: Number, zin: Number): Number
    open fun noise4d(x: Number, y: Number, z: Number, w: Number): Number
}