@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

import kotlin.js.*
import kotlin.js.Json
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

open external class Path(points: Array<Vector2> = definedExternally) : CurvePath<Vector2> {
    override var type: String
    open var currentPoint: Vector2
    open fun fromPoints(vectors: Array<Vector2>): Path /* this */
    open fun setFromPoints(vectors: Array<Vector2>): Path /* this */
    open fun moveTo(x: Number, y: Number): Path /* this */
    open fun lineTo(x: Number, y: Number): Path /* this */
    open fun quadraticCurveTo(aCPx: Number, aCPy: Number, aX: Number, aY: Number): Path /* this */
    open fun bezierCurveTo(aCP1x: Number, aCP1y: Number, aCP2x: Number, aCP2y: Number, aX: Number, aY: Number): Path /* this */
    open fun splineThru(pts: Array<Vector2>): Path /* this */
    open fun arc(aX: Number, aY: Number, aRadius: Number, aStartAngle: Number, aEndAngle: Number, aClockwise: Boolean): Path /* this */
    open fun absarc(aX: Number, aY: Number, aRadius: Number, aStartAngle: Number, aEndAngle: Number, aClockwise: Boolean): Path /* this */
    open fun ellipse(aX: Number, aY: Number, xRadius: Number, yRadius: Number, aStartAngle: Number, aEndAngle: Number, aClockwise: Boolean, aRotation: Number): Path /* this */
    open fun absellipse(aX: Number, aY: Number, xRadius: Number, yRadius: Number, aStartAngle: Number, aEndAngle: Number, aClockwise: Boolean, aRotation: Number): Path /* this */
}