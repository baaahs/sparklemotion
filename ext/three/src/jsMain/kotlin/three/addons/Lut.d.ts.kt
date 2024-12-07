package three.addons

import org.w3c.dom.HTMLCanvasElement
import three.Color

open external class Lut(colormap: String = definedExternally, numberofcolors: Number = definedExternally) {
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