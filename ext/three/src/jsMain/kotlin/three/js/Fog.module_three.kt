@file:JsModule("three")
@file:JsNonModule
package three.js

external interface FogJSON {
    var type: String
    var name: String
    var color: Number
    var near: Number
    var far: Number
}

open external class Fog {
    constructor(color: Color, near: Number = definedExternally, far: Number = definedExternally)
    constructor(color: Color)
    constructor(color: Color, near: Number = definedExternally)
    constructor(color: String, near: Number = definedExternally, far: Number = definedExternally)
    constructor(color: String)
    constructor(color: String, near: Number = definedExternally)
    constructor(color: Number, near: Number = definedExternally, far: Number = definedExternally)
    constructor(color: Number)
    constructor(color: Number, near: Number = definedExternally)
    open val isFog: Boolean
    open var name: String
    open var color: Color
    open var near: Number
    open var far: Number
    open fun clone(): Fog
    open fun toJSON(): FogJSON
}