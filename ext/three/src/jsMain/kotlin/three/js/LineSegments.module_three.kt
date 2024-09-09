@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class LineSegments<TGeometry, TMaterial>(geometry: TGeometry = definedExternally, material: TMaterial = definedExternally, mode: Number = definedExternally) : Line<TGeometry, TMaterial> {
    override var type: dynamic /* String | String */
    open var isLineSegments: Boolean
}
