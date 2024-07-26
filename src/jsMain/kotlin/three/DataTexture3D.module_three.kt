@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

import org.khronos.webgl.*

open external class DataTexture3D : Texture {
    constructor(data: Int8Array, width: Number, height: Number, depth: Number)
    constructor(data: Uint8Array, width: Number, height: Number, depth: Number)
    constructor(data: Uint8ClampedArray, width: Number, height: Number, depth: Number)
    constructor(data: Int16Array, width: Number, height: Number, depth: Number)
    constructor(data: Uint16Array, width: Number, height: Number, depth: Number)
    constructor(data: Int32Array, width: Number, height: Number, depth: Number)
    constructor(data: Uint32Array, width: Number, height: Number, depth: Number)
    constructor(data: Float32Array, width: Number, height: Number, depth: Number)
    constructor(data: Float64Array, width: Number, height: Number, depth: Number)
    override var magFilter: TextureFilter
    override var minFilter: TextureFilter
    open var wrapR: Boolean
    override var flipY: Boolean
    override var generateMipmaps: Boolean
}