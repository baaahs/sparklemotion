@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class FogExp2 : IFog {
    constructor(hex: Number, density: Number = definedExternally)
    constructor(hex: String, density: Number = definedExternally)
    override var name: String
    override var color: Color
    open var density: Number
    open var isFogExp2: Boolean
    override fun clone(): FogExp2 /* this */
    override fun toJSON(): Any
}