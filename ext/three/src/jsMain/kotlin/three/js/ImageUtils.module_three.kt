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