@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.Vector2
import three.Vector3
import three.Vector4

open external class NURBSSurface {
    constructor(degree1: Number, degree2: Number, knots1: Array<Number>, knots2: Array<Number>, controlPoints: Array<Array<Vector2>>)
    constructor(degree1: Number, degree2: Number, knots1: Array<Number>, knots2: Array<Number>, controlPoints: Array<Array<Vector3>>)
    constructor(degree1: Number, degree2: Number, knots1: Array<Number>, knots2: Array<Number>, controlPoints: Array<Array<Vector4>>)
    open fun getPoint(t1: Number, t2: Number, target: Vector3)
}