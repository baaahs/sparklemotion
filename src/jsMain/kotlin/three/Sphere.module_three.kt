@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

import kotlin.js.*
import kotlin.js.Json
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

open external class Sphere(center: Vector3 = definedExternally, radius: Number = definedExternally) {
    open var center: Vector3
    open var radius: Number
    open fun set(center: Vector3, radius: Number): Sphere
    open fun setFromPoints(points: Array<Vector3>, optionalCenter: Vector3 = definedExternally): Sphere
    open fun clone(): Sphere /* this */
    open fun copy(sphere: Sphere): Sphere /* this */
    open fun isEmpty(): Boolean
    open fun makeEmpty(): Sphere /* this */
    open fun containsPoint(point: Vector3): Boolean
    open fun distanceToPoint(point: Vector3): Number
    open fun intersectsSphere(sphere: Sphere): Boolean
    open fun intersectsBox(box: Box3): Boolean
    open fun intersectsPlane(plane: Plane): Boolean
    open fun clampPoint(point: Vector3, target: Vector3): Vector3
    open fun getBoundingBox(target: Box3): Box3
    open fun applyMatrix4(matrix: Matrix4): Sphere
    open fun translate(offset: Vector3): Sphere
    open fun equals(sphere: Sphere): Boolean
    open fun empty(): Any
}