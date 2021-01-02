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