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

external open class CatmullRomCurve3(points: Array<Vector3> = definedExternally, closed: Boolean = definedExternally, curveType: String /* "centripetal" | "chordal" | "catmullrom" */ = definedExternally, tension: Number = definedExternally) : Curve<Vector3> {
    open val isCatmullRomCurve3: Any = definedExternally /* true */
    open var override: Any
    override val type: String /* String | "CatmullRomCurve3" */
    open var closed: Boolean
    open var points: Array<Vector3>
    open var curveType: String /* "centripetal" | "chordal" | "catmullrom" */
    open var tension: Number
}