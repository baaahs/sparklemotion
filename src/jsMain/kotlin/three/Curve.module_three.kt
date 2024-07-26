@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

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