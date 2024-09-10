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

open external class InstancedInterleavedBuffer : InterleavedBuffer {
    constructor(array: Int8Array, stride: Number, meshPerAttribute: Number = definedExternally)
    constructor(array: Int8Array, stride: Number)
    constructor(array: Uint8Array, stride: Number, meshPerAttribute: Number = definedExternally)
    constructor(array: Uint8Array, stride: Number)
    constructor(array: Uint8ClampedArray, stride: Number, meshPerAttribute: Number = definedExternally)
    constructor(array: Uint8ClampedArray, stride: Number)
    constructor(array: Int16Array, stride: Number, meshPerAttribute: Number = definedExternally)
    constructor(array: Int16Array, stride: Number)
    constructor(array: Uint16Array, stride: Number, meshPerAttribute: Number = definedExternally)
    constructor(array: Uint16Array, stride: Number)
    constructor(array: Int32Array, stride: Number, meshPerAttribute: Number = definedExternally)
    constructor(array: Int32Array, stride: Number)
    constructor(array: Uint32Array, stride: Number, meshPerAttribute: Number = definedExternally)
    constructor(array: Uint32Array, stride: Number)
    constructor(array: Float32Array, stride: Number, meshPerAttribute: Number = definedExternally)
    constructor(array: Float32Array, stride: Number)
    constructor(array: Float64Array, stride: Number, meshPerAttribute: Number = definedExternally)
    constructor(array: Float64Array, stride: Number)
    open var meshPerAttribute: Number
}