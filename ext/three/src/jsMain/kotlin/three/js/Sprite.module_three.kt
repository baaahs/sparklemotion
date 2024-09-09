@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class Sprite(material: SpriteMaterial = definedExternally) : Object3D {
    override var type: String /* 'Sprite' */
    open var isSprite: Boolean
    open var geometry: BufferGeometry
    open var material: SpriteMaterial
    open var center: Vector2
    override fun raycast(raycaster: Raycaster, intersects: Array<Intersection>)
    open fun copy(source: Sprite /* this */): Sprite /* this */
}