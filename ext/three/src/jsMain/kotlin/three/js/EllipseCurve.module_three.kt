@file:JsModule("three")
@file:JsNonModule
package three.js

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