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

open external class Box2(min: Vector2 = definedExternally, max: Vector2 = definedExternally) {
    open var min: Vector2
    open var max: Vector2
    open fun set(min: Vector2, max: Vector2): Box2
    open fun setFromPoints(points: Array<Vector2>): Box2
    open fun setFromCenterAndSize(center: Vector2, size: Vector2): Box2
    open fun clone(): Box2 /* this */
    open fun copy(box: Box2): Box2 /* this */
    open fun makeEmpty(): Box2
    open fun isEmpty(): Boolean
    open fun getCenter(target: Vector2): Vector2
    open fun getSize(target: Vector2): Vector2
    open fun expandByPoint(point: Vector2): Box2
    open fun expandByVector(vector: Vector2): Box2
    open fun expandByScalar(scalar: Number): Box2
    open fun containsPoint(point: Vector2): Boolean
    open fun containsBox(box: Box2): Boolean
    open fun getParameter(point: Vector2, target: Vector2): Vector2
    open fun intersectsBox(box: Box2): Boolean
    open fun clampPoint(point: Vector2, target: Vector2): Vector2
    open fun distanceToPoint(point: Vector2): Number
    open fun intersect(box: Box2): Box2
    open fun union(box: Box2): Box2
    open fun translate(offset: Vector2): Box2
    open fun equals(box: Box2): Boolean
    open fun empty(): Any
    open fun isIntersectionBox(b: Any): Any
}