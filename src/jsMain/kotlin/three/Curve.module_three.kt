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

open external class Curve<T : Vector> {
    open var type: String
    open var arcLengthDivisions: Number
    open fun getPoint(t: Number, optionalTarget: T = definedExternally): T
    open fun getPointAt(u: Number, optionalTarget: T = definedExternally): T
    open fun getPoints(divisions: Number = definedExternally): Array<T>
    open fun getSpacedPoints(divisions: Number = definedExternally): Array<T>
    open fun getLength(): Number
    open fun getLengths(divisions: Number = definedExternally): Array<Number>
    open fun updateArcLengths()
    open fun getUtoTmapping(u: Number, distance: Number): Number
    open fun getTangent(t: Number, optionalTarget: T = definedExternally): T
    open fun getTangentAt(u: Number, optionalTarget: T = definedExternally): T
    open fun clone(): Curve<T>
    open fun copy(source: Curve<T>): Curve<T> /* this */
    open fun toJSON(): Any?
    open fun fromJSON(json: Any?): Curve<T> /* this */

    companion object {
        fun create(constructorFunc: Function<*>, getPointFunc: Function<*>): Function<*>
    }
}