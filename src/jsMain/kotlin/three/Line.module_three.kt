@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class Line<TGeometry, TMaterial>(geometry: TGeometry = definedExternally, material: TMaterial = definedExternally, mode: Number = definedExternally) : Object3D {
    open var geometry: TGeometry
    open var material: TMaterial
    override var type: dynamic /* String | String | String | String */
    open var isLine: Boolean
    open var morphTargetInfluences: Array<Number>
    open var morphTargetDictionary: `T$20`
    open fun computeLineDistances(): Line<TGeometry, TMaterial> /* this */
    override fun raycast(raycaster: Raycaster, intersects: Array<Intersection>)
    open fun updateMorphTargets()
}
