@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.js

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

external interface CurveJSON {
    var metadata: `T$0`
    var arcLengthDivisions: Number
    var type: String
}

external interface `T$7` {
    var tangents: Array<Vector3>
    var normals: Array<Vector3>
    var binormals: Array<Vector3>
}

external open class Curve<TVector> {
    open val type: String /* String | "Curve" */
    open var arcLengthDivisions: Number
    open fun getPoint(t: Number, optionalTarget: TVector = definedExternally): TVector
    open fun getPointAt(u: Number, optionalTarget: TVector = definedExternally): TVector
    open fun getPoints(divisions: Number = definedExternally): Array<TVector>
    open fun getSpacedPoints(divisions: Number = definedExternally): Array<TVector>
    open fun getLength(): Number
    open fun getLengths(divisions: Number = definedExternally): Array<Number>
    open fun updateArcLengths()
    open fun getUtoTmapping(u: Number, distance: Number): Number
    open fun getTangent(t: Number, optionalTarget: TVector = definedExternally): TVector
    open fun getTangentAt(u: Number, optionalTarget: TVector = definedExternally): TVector
    open fun computeFrenetFrames(segments: Number, closed: Boolean = definedExternally): `T$7`
    open fun clone(): Curve<TVector> /* this */
    open fun copy(source: Curve<TVector>): Curve<TVector> /* this */
    open fun toJSON(): CurveJSON
    open fun fromJSON(json: CurveJSON): Curve<TVector> /* this */
}