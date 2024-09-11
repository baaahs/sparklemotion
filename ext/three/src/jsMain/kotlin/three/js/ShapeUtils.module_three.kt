@file:JsModule("three")
@file:JsNonModule
package three.js

open external class ShapeUtils {
    companion object {
        fun area(contour: Array<Vector2Like>): Number
        fun isClockWise(pts: Array<Vector2Like>): Boolean
        fun triangulateShape(contour: Array<Vector2Like>, holes: Array<Array<Vector2Like>>): Array<Array<Number>>
    }
}