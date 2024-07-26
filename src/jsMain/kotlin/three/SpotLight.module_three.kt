@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class SpotLight : Light {
    constructor(color: Color = definedExternally, intensity: Number = definedExternally, distance: Number = definedExternally, angle: Number = definedExternally, penumbra: Number = definedExternally, decay: Number = definedExternally)
    constructor(color: String = definedExternally, intensity: Number = definedExternally, distance: Number = definedExternally, angle: Number = definedExternally, penumbra: Number = definedExternally, decay: Number = definedExternally)
    constructor(color: Number = definedExternally, intensity: Number = definedExternally, distance: Number = definedExternally, angle: Number = definedExternally, penumbra: Number = definedExternally, decay: Number = definedExternally)
    override var type: String
    override var position: Vector3
    open var target: Object3D
    override var intensity: Number
    open var distance: Number
    open var angle: Number
    open var decay: Number
    override var shadow: /*Spot*/LightShadow
    open var power: Number
    open var penumbra: Number
    open var isSpotLight: Boolean
}