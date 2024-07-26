@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class Points<TGeometry, TMaterial>(geometry: TGeometry = definedExternally, material: TMaterial = definedExternally) : Object3D {
    override var type: String /* 'Points' */
    open var morphTargetInfluences: Array<Number>
    open var morphTargetDictionary: `T$20`
    open var isPoints: Boolean
    open var geometry: TGeometry
    open var material: TMaterial
    override fun raycast(raycaster: Raycaster, intersects: Array<Intersection>)
    open fun updateMorphTargets()
}