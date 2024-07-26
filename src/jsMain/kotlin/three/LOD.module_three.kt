@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

external interface `T$25` {
    var distance: Number
    var `object`: Object3D
}

open external class LOD : Object3D {
    override var type: String /* 'LOD' */
    open var levels: Array<`T$25`>
    open var autoUpdate: Boolean
    open var isLOD: Boolean
    open fun addLevel(obj: Object3D, distance: Number = definedExternally): LOD /* this */
    open fun getCurrentLevel(): Number
    open fun getObjectForDistance(distance: Number): Object3D?
    override fun raycast(raycaster: Raycaster, intersects: Array<Intersection>)
    open fun update(camera: Camera)
    open fun toJSON(meta: Any): Any
    override fun toJSON(meta: `T$0`): Any
    open var objects: Array<Any>
}