@file:JsModule("three")
@file:JsNonModule
package three

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

open external class QuadraticBezierCurve(v0: Vector2 = definedExternally, v1: Vector2 = definedExternally, v2: Vector2 = definedExternally) : Curve<Vector2> {
    open val isQuadraticBezierCurve: Any = definedExternally /* true */
    open var override: Any
    override val type: String /* String | "QuadraticBezierCurve" */
    open var v0: Vector2
    open var v1: Vector2
    open var v2: Vector2
}