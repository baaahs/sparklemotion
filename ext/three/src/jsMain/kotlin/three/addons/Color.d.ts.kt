@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

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

external interface HSL {
    var h: Number
    var s: Number
    var l: Number
}

external interface RGB {
    var r: Number
    var g: Number
    var b: Number
}

external open class Color {
    constructor(color: Color = definedExternally)
    constructor()
    constructor(color: String = definedExternally)
    constructor(color: Number = definedExternally)
    constructor(r: Number, g: Number, b: Number)
    open val isColor: Boolean
    open var r: Number
    open var g: Number
    open var b: Number
    open fun set(vararg args: Any /* JsTuple<color, Any> | JsTuple<r, Number, g, Number, b, Number> */): Color /* this */
    open fun setFromVector3(vector: Vector3): Color /* this */
    open fun setScalar(scalar: Number): Color
    open fun setHex(hex: Number, colorSpace: Any = definedExternally): Color
    open fun setRGB(r: Number, g: Number, b: Number, colorSpace: Any = definedExternally): Color
    open fun setHSL(h: Number, s: Number, l: Number, colorSpace: Any = definedExternally): Color
    open fun setStyle(style: String, colorSpace: Any = definedExternally): Color
    open fun setColorName(style: String, colorSpace: Any = definedExternally): Color
    open fun clone(): Color /* this */
    open fun copy(color: Color): Color /* this */
    open fun copySRGBToLinear(color: Color): Color
    open fun copyLinearToSRGB(color: Color): Color
    open fun convertSRGBToLinear(): Color
    open fun convertLinearToSRGB(): Color
    open fun getHex(colorSpace: Any = definedExternally): Number
    open fun getHexString(colorSpace: Any = definedExternally): String
    open fun getHSL(target: HSL, colorSpace: Any = definedExternally): HSL
    open fun getRGB(target: RGB, colorSpace: Any = definedExternally): RGB
    open fun getStyle(colorSpace: Any = definedExternally): String
    open fun offsetHSL(h: Number, s: Number, l: Number): Color /* this */
    open fun add(color: Color): Color /* this */
    open fun addColors(color1: Color, color2: Color): Color /* this */
    open fun addScalar(s: Number): Color /* this */
    open fun applyMatrix3(m: Matrix3): Color /* this */
    open fun sub(color: Color): Color /* this */
    open fun multiply(color: Color): Color /* this */
    open fun multiplyScalar(s: Number): Color /* this */
    open fun lerp(color: Color, alpha: Number): Color /* this */
    open fun lerpColors(color1: Color, color2: Color, alpha: Number): Color /* this */
    open fun lerpHSL(color: Color, alpha: Number): Color /* this */
    open fun equals(color: Color): Boolean
    open fun fromArray(array: Array<Number>, offset: Number = definedExternally): Color /* this */
    open fun fromArray(array: Array<Number>): Color /* this */
    open fun fromArray(array: ArrayLike<Number>, offset: Number = definedExternally): Color /* this */
    open fun fromArray(array: ArrayLike<Number>): Color /* this */
    open fun toArray(array: Array<Number> = definedExternally, offset: Number = definedExternally): Array<Number>
    open fun toArray(): Array<Number>
    open fun toArray(array: Array<Number> = definedExternally): Array<Number>
    open fun toArray(xyz: ArrayLike<Number>, offset: Number = definedExternally): ArrayLike<Number>
    open fun toArray(xyz: ArrayLike<Number>): ArrayLike<Number>
    open fun toJSON(): Number
    open fun fromBufferAttribute(attribute: BufferAttribute, index: Number): Color /* this */
    open fun fromBufferAttribute(attribute: InterleavedBufferAttribute, index: Number): Color /* this */

    companion object {
        var NAMES: Any
    }
}