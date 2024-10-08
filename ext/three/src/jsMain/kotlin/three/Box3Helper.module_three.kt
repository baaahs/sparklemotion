@file:JsModule("three")
@file:JsNonModule
package three

open external class Box3Helper : LineSegments<BufferGeometry<NormalBufferAttributes>, Material> {
    constructor(box: Box3, color: Color = definedExternally)
    constructor(box: Box3)
    constructor(box: Box3, color: String = definedExternally)
    constructor(box: Box3, color: Number = definedExternally)
    override var override: Any
    override val type: String /* String | "Box3Helper" */
    open var box: Box3
    open fun dispose()
}