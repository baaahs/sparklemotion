@file:JsModule("three")
@file:JsNonModule
package three.js

external interface TextureImageData {
    var data: dynamic /* Uint8Array | Uint8ClampedArray */
        get() = definedExternally
        set(value) = definedExternally
    var height: Number
    var width: Number
}

external interface Texture3DImageData : TextureImageData {
    var depth: Number
}