package three.js

open external class Points<TGeometry : BufferGeometry<NormalOrGLBufferAttributes>, TMaterial, TEventMap : Object3DEventMap>(geometry: TGeometry = definedExternally, material: TMaterial = definedExternally) : Object3D<TEventMap> {
    open val isPoints: Boolean
    open var override: Any
    override val type: String /* String | "Points" */
    open var morphTargetInfluences: Array<Number>?
    open var morphTargetDictionary: `T$31`?
    open var geometry: TGeometry
    open var material: TMaterial
    open fun updateMorphTargets()
}