@file:JsModule("three")
@file:JsNonModule
package three

external interface FogExp2JSON {
    var type: String
    var name: String
    var color: Number
    var density: Number
}

open external class FogExp2 {
    constructor(color: Color, density: Number = definedExternally)
    constructor(color: Color)
    constructor(color: String, density: Number = definedExternally)
    constructor(color: String)
    constructor(color: Number, density: Number = definedExternally)
    constructor(color: Number)
    open val isFogExp2: Boolean
    open var name: String
    open var color: Color
    open var density: Number
    open fun clone(): FogExp2
    open fun toJSON(): FogExp2JSON
}