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

open external class ShapeUtils {
    companion object {
        fun area(contour: Array<Vector2Like>): Number
        fun isClockWise(pts: Array<Vector2Like>): Boolean
        fun triangulateShape(contour: Array<Vector2Like>, holes: Array<Array<Vector2Like>>): Array<Array<Number>>
    }
}