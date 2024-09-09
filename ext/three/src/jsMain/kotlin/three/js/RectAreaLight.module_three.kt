@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class RectAreaLight : Light {
    constructor(color: Color = definedExternally, intensity: Number = definedExternally, width: Number = definedExternally, height: Number = definedExternally)
    constructor(color: String = definedExternally, intensity: Number = definedExternally, width: Number = definedExternally, height: Number = definedExternally)
    constructor(color: Number = definedExternally, intensity: Number = definedExternally, width: Number = definedExternally, height: Number = definedExternally)
    override var type: String
    open var width: Number
    open var height: Number
    override var intensity: Number
    open var isRectAreaLight: Boolean
}