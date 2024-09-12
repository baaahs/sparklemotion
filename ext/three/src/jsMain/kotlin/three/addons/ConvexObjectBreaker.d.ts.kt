@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import kotlin.js.*
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*

external interface CutByPlaneOutput {
    var object1: Object3D__0
    var object2: Object3D__0
}

external open class ConvexObjectBreaker(minSizeForBreak: Number = definedExternally, smallDelta: Number = definedExternally) {
    open fun prepareBreakableObject(obj: Object3D__0, mass: Number, velocity: Vector3, angularVelocity: Vector3, breakable: Boolean)
    open fun subdivideByImpact(obj: Object3D__0, pointOfImpact: Vector3, normal: Vector3, maxRadialIterations: Number, maxRandomIterations: Number): Array<Object3D__0>
    open fun cutByPlane(obj: Object3D__0, plane: Plane, output: CutByPlaneOutput): Number
}