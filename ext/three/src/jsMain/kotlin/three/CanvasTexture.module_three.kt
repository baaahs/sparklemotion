@file:JsModule("three")
@file:JsNonModule
package three

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

open external class CanvasTexture : Texture {
    constructor(canvas: ImageBitmap, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally, type: Any = definedExternally, anisotropy: Number = definedExternally)
    constructor(canvas: ImageBitmap)
    constructor(canvas: ImageBitmap, mapping: Any = definedExternally)
    constructor(canvas: ImageBitmap, mapping: Any = definedExternally, wrapS: Any = definedExternally)
    constructor(canvas: ImageBitmap, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally)
    constructor(canvas: ImageBitmap, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally)
    constructor(canvas: ImageBitmap, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally)
    constructor(canvas: ImageBitmap, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally)
    constructor(canvas: ImageBitmap, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally, type: Any = definedExternally)
    constructor(canvas: ImageData, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally, type: Any = definedExternally, anisotropy: Number = definedExternally)
    constructor(canvas: ImageData)
    constructor(canvas: ImageData, mapping: Any = definedExternally)
    constructor(canvas: ImageData, mapping: Any = definedExternally, wrapS: Any = definedExternally)
    constructor(canvas: ImageData, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally)
    constructor(canvas: ImageData, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally)
    constructor(canvas: ImageData, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally)
    constructor(canvas: ImageData, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally)
    constructor(canvas: ImageData, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally, type: Any = definedExternally)
    constructor(canvas: HTMLImageElement, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally, type: Any = definedExternally, anisotropy: Number = definedExternally)
    constructor(canvas: HTMLImageElement)
    constructor(canvas: HTMLImageElement, mapping: Any = definedExternally)
    constructor(canvas: HTMLImageElement, mapping: Any = definedExternally, wrapS: Any = definedExternally)
    constructor(canvas: HTMLImageElement, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally)
    constructor(canvas: HTMLImageElement, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally)
    constructor(canvas: HTMLImageElement, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally)
    constructor(canvas: HTMLImageElement, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally)
    constructor(canvas: HTMLImageElement, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally, type: Any = definedExternally)
    constructor(canvas: HTMLCanvasElement, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally, type: Any = definedExternally, anisotropy: Number = definedExternally)
    constructor(canvas: HTMLCanvasElement)
    constructor(canvas: HTMLCanvasElement, mapping: Any = definedExternally)
    constructor(canvas: HTMLCanvasElement, mapping: Any = definedExternally, wrapS: Any = definedExternally)
    constructor(canvas: HTMLCanvasElement, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally)
    constructor(canvas: HTMLCanvasElement, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally)
    constructor(canvas: HTMLCanvasElement, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally)
    constructor(canvas: HTMLCanvasElement, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally)
    constructor(canvas: HTMLCanvasElement, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally, type: Any = definedExternally)
    constructor(canvas: HTMLVideoElement, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally, type: Any = definedExternally, anisotropy: Number = definedExternally)
    constructor(canvas: HTMLVideoElement)
    constructor(canvas: HTMLVideoElement, mapping: Any = definedExternally)
    constructor(canvas: HTMLVideoElement, mapping: Any = definedExternally, wrapS: Any = definedExternally)
    constructor(canvas: HTMLVideoElement, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally)
    constructor(canvas: HTMLVideoElement, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally)
    constructor(canvas: HTMLVideoElement, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally)
    constructor(canvas: HTMLVideoElement, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally)
    constructor(canvas: HTMLVideoElement, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally, type: Any = definedExternally)
    constructor(canvas: OffscreenCanvas, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally, type: Any = definedExternally, anisotropy: Number = definedExternally)
    constructor(canvas: OffscreenCanvas)
    constructor(canvas: OffscreenCanvas, mapping: Any = definedExternally)
    constructor(canvas: OffscreenCanvas, mapping: Any = definedExternally, wrapS: Any = definedExternally)
    constructor(canvas: OffscreenCanvas, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally)
    constructor(canvas: OffscreenCanvas, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally)
    constructor(canvas: OffscreenCanvas, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally)
    constructor(canvas: OffscreenCanvas, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally)
    constructor(canvas: OffscreenCanvas, mapping: Any = definedExternally, wrapS: Any = definedExternally, wrapT: Any = definedExternally, magFilter: Any = definedExternally, minFilter: Any = definedExternally, format: Any = definedExternally, type: Any = definedExternally)
    open val isCanvasTexture: Boolean
}