@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.AnimationMixer
import three.Mesh
import three.Object3D
import three.Texture

external interface MD2PartsConfig {
    var baseUrl: String
    var body: String
    var skins: Array<String>
    var weapons: Array<dynamic /* JsTuple<String, String> */>
}

open external class MD2Character {
    open var scale: Number
    open var animationFPS: Number
    open var root: Object3D
    open var meshBody: Mesh<*, *>?
    open var meshWeapon: Mesh<*, *>?
    open var skinsBody: Array<Texture>
    open var skinsWeapon: Array<Texture>
    open var weapons: Array<Mesh<*, *>>
    open var activeAnimation: String?
    open var mixer: AnimationMixer?
    open var loadCounter: Number
    open fun onLoadComplete()
    open fun loadParts(config: MD2PartsConfig)
    open fun setPlaybackRate(rate: Number)
    open fun setWireframe(wireframeEnabled: Boolean)
    open fun setSkin(index: Number)
    open fun setWeapon(index: Number)
    open fun setAnimation(clipName: String)
    open fun syncWeaponAnimation()
    open fun update(delta: Number)
}