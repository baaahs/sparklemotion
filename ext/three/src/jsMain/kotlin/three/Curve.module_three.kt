@file:JsModule("three")
@file:JsNonModule
package three

external interface CurveJSON {
    var metadata: `T$0_Object3D`
    var arcLengthDivisions: Number
    var type: String
}

external interface `T$7` {
    var tangents: Array<Vector3>
    var normals: Array<Vector3>
    var binormals: Array<Vector3>
}

open external class Curve<TVector> {
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