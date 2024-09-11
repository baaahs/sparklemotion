@file:JsModule("three")
@file:JsNonModule
package three.js

external interface CurvePathJSON : CurveJSON {
    var autoClose: Boolean
    var curves: Array<CurveJSON>
}

open external class CurvePath<TVector> : Curve<TVector> {
    open var override: Any
    override val type: String /* String | "CurvePath" */
    open var curves: Array<Curve<TVector>>
    open var autoClose: Boolean
    open fun add(curve: Curve<TVector>)
    open fun closePath(): CurvePath<TVector> /* this */
    override fun getPoint(t: Number, optionalTarget: TVector): TVector
    open fun getCurveLengths(): Array<Number>
    override fun getPoints(divisions: Number): Array<TVector>
    override fun getSpacedPoints(divisions: Number): Array<TVector>
    override fun toJSON(): CurvePathJSON
    open fun fromJSON(json: CurvePathJSON): CurvePath<TVector> /* this */
    override fun fromJSON(json: CurveJSON): Curve<TVector> /* this */
}