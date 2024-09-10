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

external interface PathJSON : CurvePathJSON {
    var currentPoint: dynamic /* JsTuple<x, Number, y, Number> */
        get() = definedExternally
        set(value) = definedExternally
}

open external class Path(points: Array<Vector2> = definedExternally) : CurvePath<Vector2> {
    override var override: Any
    override val type: String /* String | "Path" */
    open var currentPoint: Vector2
    open fun absarc(aX: Number, aY: Number, aRadius: Number, aStartAngle: Number, aEndAngle: Number, aClockwise: Boolean = definedExternally): Path /* this */
    open fun absellipse(aX: Number, aY: Number, xRadius: Number, yRadius: Number, aStartAngle: Number, aEndAngle: Number, aClockwise: Boolean = definedExternally, aRotation: Number = definedExternally): Path /* this */
    open fun arc(aX: Number, aY: Number, aRadius: Number, aStartAngle: Number, aEndAngle: Number, aClockwise: Boolean = definedExternally): Path /* this */
    open fun bezierCurveTo(aCP1x: Number, aCP1y: Number, aCP2x: Number, aCP2y: Number, aX: Number, aY: Number): Path /* this */
    open fun ellipse(aX: Number, aY: Number, xRadius: Number, yRadius: Number, aStartAngle: Number, aEndAngle: Number, aClockwise: Boolean = definedExternally, aRotation: Number = definedExternally): Path /* this */
    open fun lineTo(x: Number, y: Number): Path /* this */
    open fun moveTo(x: Number, y: Number): Path /* this */
    open fun quadraticCurveTo(aCPx: Number, aCPy: Number, aX: Number, aY: Number): Path /* this */
    open fun setFromPoints(vectors: Array<Vector2>): Path /* this */
    open fun splineThru(pts: Array<Vector2>): Path /* this */
    override fun toJSON(): PathJSON
    open fun fromJSON(json: PathJSON): Path /* this */
}