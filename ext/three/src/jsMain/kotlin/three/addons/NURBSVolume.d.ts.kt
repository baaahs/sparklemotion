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

external open class NURBSVolume(degree1: Number, degree2: Number, degree3: Number, knots1: Array<Number>, knots2: Array<Number>, knots3: Array<Number>, controlPoints: Array<Array<Array<Vector4>>>) {
    open var degree1: Number
    open var degree2: Number
    open var degree3: Number
    open var knots1: Array<Number>
    open var knots2: Array<Number>
    open var knots3: Array<Number>
    open var controlPoints: Array<Array<Array<Vector4>>>
    open fun getPoint(t1: Number, t2: Number, t3: Number, target: Vector3)
}