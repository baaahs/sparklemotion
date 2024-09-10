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

open external class ShapePath {
    open val type: String /* "ShapePath" */
    open var subPaths: Array<Path>
    open val currentPath: Path?
    open var color: Color
    open fun moveTo(x: Number, y: Number): ShapePath /* this */
    open fun lineTo(x: Number, y: Number): ShapePath /* this */
    open fun quadraticCurveTo(aCPx: Number, aCPy: Number, aX: Number, aY: Number): ShapePath /* this */
    open fun bezierCurveTo(aCP1x: Number, aCP1y: Number, aCP2x: Number, aCP2y: Number, aX: Number, aY: Number): ShapePath /* this */
    open fun splineThru(pts: Array<Vector2>): ShapePath /* this */
    open fun toShapes(isCCW: Boolean): Array<Shape>
}