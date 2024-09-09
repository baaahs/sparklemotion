@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class AmbientLight : Light {
    constructor(color: Color = definedExternally, intensity: Number = definedExternally)
    constructor(color: String = definedExternally, intensity: Number = definedExternally)
    constructor(color: Number = definedExternally, intensity: Number = definedExternally)
    override var type: String
    override var castShadow: Boolean
    open var isAmbientLight: Boolean
}