package three.addons

import three.Mesh
import three.Object3D
import three.Texture

external interface `T$86` {
    var crouch: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var jump: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var attack: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var moveForward: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var moveBackward: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var moveLeft: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var moveRight: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

open external class MD2CharacterComplex {
    open var scale: Number
    open var animationFPS: Number
    open var transitionFrames: Number
    open var maxSpeed: Number
    open var maxReverseSpeed: Number
    open var frontAcceleration: Number
    open var backAcceleration: Number
    open var frontDecceleration: Number
    open var angularSpeed: Number
    open var root: Object3D
    open var meshBody: Mesh<*, *>?
    open var meshWeapon: Mesh<*, *>?
    open var controls: `T$86`?
    open var skinsBody: Array<Texture>
    open var skinsWeapon: Array<Texture>
    open var weapons: Array<Mesh<*, *>>
    open var currentSkin: Number
    open var onLoadComplete: () -> Unit
    open var meshes: Array<Mesh<*, *>>
    open var animations: Array<Any?>
    open var loadCounter: Number
    open var speed: Number
    open var bodyOrientation: Number
    open var walkSpeed: Number
    open var crouchSpeed: Number
    open var activeAnimation: String
    open var oldAnimation: String
    open fun enableShadows(enable: Boolean)
    open fun setVisible(enable: Boolean)
    open fun shareParts(original: MD2CharacterComplex)
    open fun loadParts(config: Any?)
    open fun setPlaybackRate(rate: Number)
    open fun setWireframe(wireframeEnabled: Boolean)
    open fun setSkin(index: Number)
    open fun setWeapon(index: Number)
    open fun setAnimation(animationName: String)
    open fun update(delta: Number)
    open fun updateAnimations(delta: Number)
    open fun updateBehaviors()
    open fun updateMovementModel(delta: Number)
}