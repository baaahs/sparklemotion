@file:JsModule("three")
@file:JsNonModule
package three

open external class Line<TGeometry : BufferGeometry<NormalBufferAttributes>, TMaterial/*, TEventMap : Object3DEventMap*/: Material>(geometry: TGeometry = definedExternally, material: TMaterial = definedExternally) : Object3D/*<Object3DEventMap>*/ {
    open val isLine: Boolean
    open var override: Any
    override val type: String /* String | "Line" */
    open var geometry: TGeometry
    open var material: TMaterial
    open var morphTargetInfluences: Array<Number>?
    open var morphTargetDictionary: `T$31`?
    open fun computeLineDistances(): Line<TGeometry, TMaterial> /* this */
    open fun updateMorphTargets()
}

//open external class Line__0 : Line<BufferGeometry<NormalBufferAttributes>, dynamic /* Material | Array<Material> */, Object3DEventMap>