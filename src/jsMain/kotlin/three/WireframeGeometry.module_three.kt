@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class WireframeGeometry : BufferGeometry {
    constructor(geometry: Geometry)
    constructor(geometry: BufferGeometry)
    override var type: String
}