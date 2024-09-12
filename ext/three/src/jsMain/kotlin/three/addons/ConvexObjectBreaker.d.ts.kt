@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.Object3D
import three.Plane
import three.Vector3

external interface CutByPlaneOutput {
    var object1: Object3D
    var object2: Object3D
}

open external class ConvexObjectBreaker(minSizeForBreak: Number = definedExternally, smallDelta: Number = definedExternally) {
    open fun prepareBreakableObject(obj: Object3D, mass: Number, velocity: Vector3, angularVelocity: Vector3, breakable: Boolean)
    open fun subdivideByImpact(obj: Object3D, pointOfImpact: Vector3, normal: Vector3, maxRadialIterations: Number, maxRandomIterations: Number): Array<Object3D>
    open fun cutByPlane(obj: Object3D, plane: Plane, output: CutByPlaneOutput): Number
}