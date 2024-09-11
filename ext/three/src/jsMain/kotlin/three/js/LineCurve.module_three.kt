@file:JsModule("three")
@file:JsNonModule
package three.js

open external class LineCurve(v1: Vector2 = definedExternally, v2: Vector2 = definedExternally) : Curve<Vector2> {
    open val isLineCurve: Any = definedExternally /* true */
    open var override: Any
    override val type: String /* String | "LineCurve" */
    open var v1: Vector2
    open var v2: Vector2
}