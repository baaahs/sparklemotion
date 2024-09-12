@file:JsModule("three")
@file:JsNonModule
package three

open external class LineLoop<TGeometry : BufferGeometry<NormalBufferAttributes>, TMaterial : Material/*, TEventMap : Object3DEventMap*/>(geometry: TGeometry = definedExternally, material: TMaterial = definedExternally) : Line<TGeometry, TMaterial> {
    open val isLineLoop: Boolean
    override var override: Any
    override val type: String /* String | "LineLoop" */
}