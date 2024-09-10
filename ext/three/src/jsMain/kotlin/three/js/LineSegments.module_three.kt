package three.js

open external class LineSegments<TGeometry : BufferGeometry__0, TMaterial, TEventMap : Object3DEventMap>(geometry: TGeometry = definedExternally, material: TMaterial = definedExternally) : Line<TGeometry, TMaterial, TEventMap> {
    open val isLineSegments: Boolean
    override var override: Any
    override val type: String /* String | "LineSegments" */
}

typealias LineSegments__0 = LineSegments<BufferGeometry__0, dynamic /* Material | Array<Material> */, Object3DEventMap>

open external class LineSegments__2<TGeometry : BufferGeometry__0, TMaterial> : LineSegments<TGeometry, TMaterial, Object3DEventMap>
