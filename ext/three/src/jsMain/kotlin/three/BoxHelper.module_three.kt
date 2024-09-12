@file:JsModule("three")
@file:JsNonModule
package three

open external class BoxHelper : LineSegments<BufferGeometry<NormalBufferAttributes>, LineBasicMaterial> {
    constructor(obj: Object3D/*<Object3DEventMap>*/, color: Color = definedExternally)
    constructor(obj: Object3D/*<Object3DEventMap>*/)
    constructor(obj: Object3D/*<Object3DEventMap>*/, color: String = definedExternally)
    constructor(obj: Object3D/*<Object3DEventMap>*/, color: Number = definedExternally)
    override var override: Any
    override val type: String /* String | "BoxHelper" */
    open fun update(obj: Object3D/*<Object3DEventMap>*/ = definedExternally)
    open fun setFromObject(obj: Object3D/*<Object3DEventMap>*/): BoxHelper /* this */
    open fun dispose()
}