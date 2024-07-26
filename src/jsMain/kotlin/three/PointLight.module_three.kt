@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class PointLight : Light {
    constructor(color: Color = definedExternally, intensity: Number = definedExternally, distance: Number = definedExternally, decay: Number = definedExternally)
    constructor(color: String = definedExternally, intensity: Number = definedExternally, distance: Number = definedExternally, decay: Number = definedExternally)
    constructor(color: Number = definedExternally, intensity: Number = definedExternally, distance: Number = definedExternally, decay: Number = definedExternally)
    override var type: String
    override var intensity: Number
    open var distance: Number
    open var decay: Number
    override var shadow: /*Point*/LightShadow
    open var power: Number
}