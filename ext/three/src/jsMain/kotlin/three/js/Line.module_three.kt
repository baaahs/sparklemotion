package three.js

open external class Line<TGeometry : BufferGeometry__0, TMaterial, TEventMap : Object3DEventMap>(geometry: TGeometry = definedExternally, material: TMaterial = definedExternally) : Object3D<TEventMap> {
    open val isLine: Boolean
    open var override: Any
    override val type: String /* String | "Line" */
    open var geometry: TGeometry
    open var material: TMaterial
    open var morphTargetInfluences: Array<Number>?
    open var morphTargetDictionary: `T$31`?
    open fun computeLineDistances(): Line<TGeometry, TMaterial, TEventMap> /* this */
    open fun updateMorphTargets()
}

open external class Line__0 : Line<BufferGeometry__0, dynamic /* Material | Array<Material> */, Object3DEventMap>