package three.addons

import three.Vector2
import three.Vector3
import three.Vector4

open external class NURBSCurve : Curve {
    constructor(degree: Number, knots: Array<Number>, controlPoints: Array<Vector2>, startKnot: Number = definedExternally, endKnot: Number = definedExternally)
    constructor(degree: Number, knots: Array<Number>, controlPoints: Array<Vector2>)
    constructor(degree: Number, knots: Array<Number>, controlPoints: Array<Vector2>, startKnot: Number = definedExternally)
    constructor(degree: Number, knots: Array<Number>, controlPoints: Array<Vector3>, startKnot: Number = definedExternally, endKnot: Number = definedExternally)
    constructor(degree: Number, knots: Array<Number>, controlPoints: Array<Vector3>)
    constructor(degree: Number, knots: Array<Number>, controlPoints: Array<Vector3>, startKnot: Number = definedExternally)
    constructor(degree: Number, knots: Array<Number>, controlPoints: Array<Vector4>, startKnot: Number = definedExternally, endKnot: Number = definedExternally)
    constructor(degree: Number, knots: Array<Number>, controlPoints: Array<Vector4>)
    constructor(degree: Number, knots: Array<Number>, controlPoints: Array<Vector4>, startKnot: Number = definedExternally)
    open var degree: Number
    open var knots: Array<Number>
    open var controlPoints: dynamic /* Array<Vector2> | Array<Vector3> | Array<Vector4> */
    open var startKnot: Number
    open var endKnot: Number

    override fun getPointAt(u: Number): Vector3
    override fun getTangentAt(u: Number): Vector3
}