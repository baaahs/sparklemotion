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

open external class InstancedBufferAttribute : BufferAttribute {
    constructor(array: Int8Array, itemSize: Number, normalized: Boolean = definedExternally, meshPerAttribute: Number = definedExternally)
    constructor(array: Int8Array, itemSize: Number)
    constructor(array: Int8Array, itemSize: Number, normalized: Boolean = definedExternally)
    constructor(array: Uint8Array, itemSize: Number, normalized: Boolean = definedExternally, meshPerAttribute: Number = definedExternally)
    constructor(array: Uint8Array, itemSize: Number)
    constructor(array: Uint8Array, itemSize: Number, normalized: Boolean = definedExternally)
    constructor(array: Uint8ClampedArray, itemSize: Number, normalized: Boolean = definedExternally, meshPerAttribute: Number = definedExternally)
    constructor(array: Uint8ClampedArray, itemSize: Number)
    constructor(array: Uint8ClampedArray, itemSize: Number, normalized: Boolean = definedExternally)
    constructor(array: Int16Array, itemSize: Number, normalized: Boolean = definedExternally, meshPerAttribute: Number = definedExternally)
    constructor(array: Int16Array, itemSize: Number)
    constructor(array: Int16Array, itemSize: Number, normalized: Boolean = definedExternally)
    constructor(array: Uint16Array, itemSize: Number, normalized: Boolean = definedExternally, meshPerAttribute: Number = definedExternally)
    constructor(array: Uint16Array, itemSize: Number)
    constructor(array: Uint16Array, itemSize: Number, normalized: Boolean = definedExternally)
    constructor(array: Int32Array, itemSize: Number, normalized: Boolean = definedExternally, meshPerAttribute: Number = definedExternally)
    constructor(array: Int32Array, itemSize: Number)
    constructor(array: Int32Array, itemSize: Number, normalized: Boolean = definedExternally)
    constructor(array: Uint32Array, itemSize: Number, normalized: Boolean = definedExternally, meshPerAttribute: Number = definedExternally)
    constructor(array: Uint32Array, itemSize: Number)
    constructor(array: Uint32Array, itemSize: Number, normalized: Boolean = definedExternally)
    constructor(array: Float32Array, itemSize: Number, normalized: Boolean = definedExternally, meshPerAttribute: Number = definedExternally)
    constructor(array: Float32Array, itemSize: Number)
    constructor(array: Float32Array, itemSize: Number, normalized: Boolean = definedExternally)
    constructor(array: Float64Array, itemSize: Number, normalized: Boolean = definedExternally, meshPerAttribute: Number = definedExternally)
    constructor(array: Float64Array, itemSize: Number)
    constructor(array: Float64Array, itemSize: Number, normalized: Boolean = definedExternally)
    open var meshPerAttribute: Number
    open val isInstancedBufferAttribute: Boolean
}