package three.js

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