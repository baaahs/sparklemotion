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

open external class LineCurve3(v1: Vector3 = definedExternally, v2: Vector3 = definedExternally) : Curve<Vector3> {
    open val isLineCurve3: Any = definedExternally /* true */
    open var override: Any
    override val type: String /* String | "LineCurve3" */
    open var v1: Vector3
    open var v2: Vector3
}