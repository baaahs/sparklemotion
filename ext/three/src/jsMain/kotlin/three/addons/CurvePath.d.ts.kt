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

external interface CurvePathJSON : CurveJSON {
    var autoClose: Boolean
    var curves: Array<CurveJSON>
}

external open class CurvePath<TVector> : Curve<TVector> {
    open var override: Any
    open val type: String /* String | "CurvePath" */
    open var curves: Array<Curve<TVector>>
    open var autoClose: Boolean
    open fun add(curve: Curve<TVector>)
    open fun closePath(): CurvePath<TVector> /* this */
    open fun getPoint(t: Number, optionalTarget: TVector = definedExternally): TVector
    open fun getCurveLengths(): Array<Number>
    open fun getPoints(divisions: Number = definedExternally): Array<TVector>
    open fun getSpacedPoints(divisions: Number = definedExternally): Array<TVector>
    open fun toJSON(): CurvePathJSON
    open fun fromJSON(json: CurvePathJSON): CurvePath<TVector> /* this */
}