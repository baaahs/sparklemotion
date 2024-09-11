@file:JsModule("three")
@file:JsNonModule
package three.js

open external class Cylindrical(radius: Number = definedExternally, theta: Number = definedExternally, y: Number = definedExternally) {
    open var radius: Number
    open var theta: Number
    open var y: Number
    open fun clone(): Cylindrical /* this */
    open fun copy(other: Cylindrical): Cylindrical /* this */
    open fun set(radius: Number, theta: Number, y: Number): Cylindrical /* this */
    open fun setFromVector3(vec3: Vector3): Cylindrical /* this */
    open fun setFromCartesianCoords(x: Number, y: Number, z: Number): Cylindrical /* this */
}