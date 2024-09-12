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

external open class OBB(center: Vector3 = definedExternally, halfSize: Vector3 = definedExternally, rotation: Matrix3 = definedExternally) {
    open var center: Vector3
    open var halfSize: Vector3
    open var rotation: Matrix3
    open fun set(center: Vector3, halfSize: Vector3, rotation: Matrix3): OBB /* this */
    open fun copy(obb: OBB): OBB /* this */
    open fun clone(): OBB /* this */
    open fun getSize(result: Vector3): Vector3
    open fun clampPoint(point: Vector3, result: Vector3): Vector3
    open fun containsPoint(point: Vector3): Boolean
    open fun intersectsBox3(box3: Box3): Boolean
    open fun intersectsSphere(sphere: Sphere): Boolean
    open fun intersectsOBB(obb: OBB, epsilon: Number = definedExternally): Boolean
    open fun intersectsPlane(plane: Plane): Boolean
    open fun intersectRay(ray: Ray, result: Vector3): Vector3?
    open fun intersectsRay(ray: Ray): Boolean
    open fun fromBox3(box3: Box3): OBB /* this */
    open fun equals(obb: OBB): Boolean
    open fun applyMatrix4(matrix: Matrix4): OBB /* this */
}