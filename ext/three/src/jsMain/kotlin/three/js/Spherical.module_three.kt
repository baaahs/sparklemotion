@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class Spherical(radius: Number = definedExternally, phi: Number = definedExternally, theta: Number = definedExternally) {
    open var radius: Number
    open var phi: Number
    open var theta: Number
    open fun set(radius: Number, phi: Number, theta: Number): Spherical /* this */
    open fun clone(): Spherical /* this */
    open fun copy(other: Spherical): Spherical /* this */
    open fun makeSafe(): Spherical /* this */
    open fun setFromVector3(v: Vector3): Spherical /* this */
    open fun setFromCartesianCoords(x: Number, y: Number, z: Number): Spherical /* this */
}