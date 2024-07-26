@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

external interface HSL {
    var h: Number
    var s: Number
    var l: Number
}

open external class Color {
    constructor(color: Color = definedExternally)
    constructor(color: String = definedExternally)
    constructor(color: Number = definedExternally)
    constructor(r: Number, g: Number, b: Number)
    open var isColor: Boolean
    open var r: Double
    open var g: Double
    open var b: Double
    open fun set(color: Color): Color
    open fun set(color: String): Color
    open fun set(color: Number): Color
    open fun setScalar(scalar: Number): Color
    open fun setHex(hex: Number): Color
    open fun setRGB(r: Number, g: Number, b: Number): Color
    open fun setHSL(h: Number, s: Number, l: Number): Color
    open fun setStyle(style: String): Color
    open fun setColorName(style: String): Color
    open fun clone(): Color /* this */
    open fun copy(color: Color): Color /* this */
    open fun copyGammaToLinear(color: Color, gammaFactor: Number = definedExternally): Color
    open fun copyLinearToGamma(color: Color, gammaFactor: Number = definedExternally): Color
    open fun convertGammaToLinear(gammaFactor: Number = definedExternally): Color
    open fun convertLinearToGamma(gammaFactor: Number = definedExternally): Color
    open fun copySRGBToLinear(color: Color): Color
    open fun copyLinearToSRGB(color: Color): Color
    open fun convertSRGBToLinear(): Color
    open fun convertLinearToSRGB(): Color
    open fun getHex(): Number
    open fun getHexString(): String
    open fun getHSL(target: HSL): HSL
    open fun getStyle(): String
    open fun offsetHSL(h: Number, s: Number, l: Number): Color /* this */
    open fun add(color: Color): Color /* this */
    open fun addColors(color1: Color, color2: Color): Color /* this */
    open fun addScalar(s: Number): Color /* this */
    open fun sub(color: Color): Color /* this */
    open fun multiply(color: Color): Color /* this */
    open fun multiplyScalar(s: Number): Color /* this */
    open fun lerp(color: Color, alpha: Number): Color /* this */
    open fun lerpHSL(color: Color, alpha: Number): Color /* this */
    open fun equals(color: Color): Boolean
    open fun fromArray(array: Array<Number>, offset: Number = definedExternally): Color /* this */
    open fun fromArray(array: ArrayLike<Number>, offset: Number = definedExternally): Color /* this */
    open fun toArray(array: Array<Number> = definedExternally, offset: Number = definedExternally): Array<Number>
    open fun toArray(xyz: ArrayLike<Number>, offset: Number = definedExternally): ArrayLike<Number>
    open fun fromBufferAttribute(attribute: BufferAttribute, index: Number): Color /* this */
}