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