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

external open class NURBSCurve : Curve<Vector3> {
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
}