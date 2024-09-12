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

external open class NURBSSurface {
    constructor(degree1: Number, degree2: Number, knots1: Array<Number>, knots2: Array<Number>, controlPoints: Array<Array<Vector2>>)
    constructor(degree1: Number, degree2: Number, knots1: Array<Number>, knots2: Array<Number>, controlPoints: Array<Array<Vector3>>)
    constructor(degree1: Number, degree2: Number, knots1: Array<Number>, knots2: Array<Number>, controlPoints: Array<Array<Vector4>>)
    open fun getPoint(t1: Number, t2: Number, target: Vector3)
}