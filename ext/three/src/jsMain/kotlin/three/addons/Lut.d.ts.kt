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

external open class Lut(colormap: String = definedExternally, numberofcolors: Number = definedExternally) {
    open var lut: Array<Color>
    open var map: Array<Any?>
    open var n: Number
    open var minV: Number
    open var maxV: Number
    open fun set(value: Lut): Lut /* this */
    open fun setMin(min: Number): Lut /* this */
    open fun setMax(max: Number): Lut /* this */
    open fun setColorMap(colormap: String = definedExternally, numberofcolors: Number = definedExternally): Lut /* this */
    open fun copy(lut: Lut): Lut /* this */
    open fun getColor(alpha: Number): Color
    open fun addColorMap(colormapName: String, arrayOfColors: Array<Array<Number>>)
    open fun createCanvas(): HTMLCanvasElement
    open fun updateCanvas(canvas: HTMLCanvasElement): HTMLCanvasElement
}

external interface ColorMapKeywords {
    var rainbow: Array<Array<Number>>
    var cooltowarm: Array<Array<Number>>
    var blackbody: Array<Array<Number>>
    var grayscale: Array<Array<Number>>
}