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

open external class EllipseCurve(aX: Number = definedExternally, aY: Number = definedExternally, xRadius: Number = definedExternally, yRadius: Number = definedExternally, aStartAngle: Number = definedExternally, aEndAngle: Number = definedExternally, aClockwise: Boolean = definedExternally, aRotation: Number = definedExternally) : Curve<Vector2> {
    open val isEllipseCurve: Any = definedExternally /* true */
    open var override: Any
    override val type: String /* String | "EllipseCurve" */
    open var aX: Number
    open var aY: Number
    open var xRadius: Number
    open var yRadius: Number
    open var aStartAngle: Number
    open var aEndAngle: Number
    open var aClockwise: Boolean
    open var aRotation: Number
}