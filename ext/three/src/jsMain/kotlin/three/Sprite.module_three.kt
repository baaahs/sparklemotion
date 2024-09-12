@file:JsModule("three")
@file:JsNonModule
package three

open external class Sprite<TEventMap : Object3DEventMap>(material: SpriteMaterial = definedExternally) : Object3D/*<Object3DEventMap>*/ {
    open val isSprite: Boolean
    open var override: Any
    override val type: String /* String | "Sprite" */
    override var castShadow: Boolean
    open var geometry: BufferGeometry<NormalBufferAttributes>
    open var material: SpriteMaterial
    open var center: Vector2
}