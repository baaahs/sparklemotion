@file:JsModule("three")
@file:JsNonModule
package three.js

import org.khronos.webgl.Uint8ClampedArray
import org.w3c.dom.*
import org.w3c.dom.svg.SVGImageElement

external interface `T$70` {
    var data: Uint8ClampedArray
    var width: Number
    var height: Number
}

open external class ImageUtils {
    companion object {
        fun getDataURL(image: HTMLImageElement): String
        fun getDataURL(image: HTMLCanvasElement): String
        fun getDataURL(image: SVGImageElement): String
        fun getDataURL(image: HTMLVideoElement): String
        fun getDataURL(image: ImageBitmap): String
        fun getDataURL(image: OffscreenCanvas): String
        fun getDataURL(image: ImageData): String
        fun sRGBToLinear(image: HTMLImageElement): HTMLCanvasElement
        fun sRGBToLinear(image: HTMLCanvasElement): HTMLCanvasElement
        fun sRGBToLinear(image: ImageBitmap): HTMLCanvasElement
        fun sRGBToLinear(image: ImageData): `T$70`
    }
}