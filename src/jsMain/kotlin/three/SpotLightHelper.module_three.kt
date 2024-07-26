@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class SpotLightHelper : Object3D {
    constructor(light: Light, color: Color = definedExternally)
    constructor(light: Light, color: String = definedExternally)
    constructor(light: Light, color: Number = definedExternally)
    open var light: Light
    override var matrix: Matrix4
    override var matrixAutoUpdate: Boolean
    open var color: dynamic /* Color? | String? | Number? */
    open var cone: LineSegments<dynamic, dynamic>
    open fun dispose()
    open fun update()
}