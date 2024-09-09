@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class LineLoop<TGeometry, TMaterial>(geometry: TGeometry = definedExternally, material: TMaterial = definedExternally) : Line<TGeometry, TMaterial> {
    override var type: String /* 'LineLoop' */
    open var isLineLoop: Boolean
}