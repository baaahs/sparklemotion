@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import kotlin.js.*
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*

external interface MD2PartsConfig {
    var baseUrl: String
    var body: String
    var skins: Array<String>
    var weapons: Array<dynamic /* JsTuple<String, String> */>
}

external open class MD2Character {
    open var scale: Number
    open var animationFPS: Number
    open var root: Object3D__0
    open var meshBody: Mesh__0?
    open var meshWeapon: Mesh__0?
    open var skinsBody: Array<Texture>
    open var skinsWeapon: Array<Texture>
    open var weapons: Array<Mesh__0>
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