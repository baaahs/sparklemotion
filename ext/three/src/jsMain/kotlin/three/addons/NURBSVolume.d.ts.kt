@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.Vector3
import three.Vector4

open external class NURBSVolume(degree1: Number, degree2: Number, degree3: Number, knots1: Array<Number>, knots2: Array<Number>, knots3: Array<Number>, controlPoints: Array<Array<Array<Vector4>>>) {
    open var degree1: Number
    open var degree2: Number
    open var degree3: Number
    open var knots1: Array<Number>
    open var knots2: Array<Number>
    open var knots3: Array<Number>
    open var controlPoints: Array<Array<Array<Vector4>>>
    open fun getPoint(t1: Number, t2: Number, t3: Number, target: Vector3)
}