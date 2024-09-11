@file:JsModule("three")
@file:JsNonModule
package three.js

open external class SplineCurve(points: Array<Vector2> = definedExternally) : Curve<Vector2> {
    open val isSplineCurve: Any = definedExternally /* true */
    open var override: Any
    override val type: String /* String | "SplineCurve" */
    open var points: Array<Vector2>
}