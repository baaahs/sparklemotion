@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class DirectionalLight : Light {
    constructor(color: Color = definedExternally, intensity: Number = definedExternally)
    constructor(color: String = definedExternally, intensity: Number = definedExternally)
    constructor(color: Number = definedExternally, intensity: Number = definedExternally)
    override var type: String
    override var position: Vector3
    open var target: Object3D
    override var intensity: Number
    override var shadow: /*Directional*/LightShadow
    open var isDirectionalLight: Boolean
}