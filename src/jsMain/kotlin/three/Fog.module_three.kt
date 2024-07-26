@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

external interface IFog {
    var name: String
    var color: Color
    fun clone(): IFog /* this */
    fun toJSON(): Any
}

open external class Fog : IFog {
    constructor(color: Color, near: Number = definedExternally, far: Number = definedExternally)
    constructor(color: Number, near: Number = definedExternally, far: Number = definedExternally)
    constructor(color: String, near: Number = definedExternally, far: Number = definedExternally)
    override var name: String
    override var color: Color
    open var near: Number
    open var far: Number
    open var isFog: Boolean
    override fun clone(): Fog /* this */
    override fun toJSON(): Any
}