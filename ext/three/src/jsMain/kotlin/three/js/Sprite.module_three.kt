package three.js

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

open external class Sprite<TEventMap : Object3DEventMap>(material: SpriteMaterial = definedExternally) : Object3D<TEventMap> {
    open val isSprite: Boolean
    open var override: Any
    override val type: String /* String | "Sprite" */
    override var castShadow: Boolean
    open var geometry: BufferGeometry__0
    open var material: SpriteMaterial
    open var center: Vector2
}

typealias Sprite__0 = Sprite<Object3DEventMap>