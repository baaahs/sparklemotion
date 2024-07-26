@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class DirectionalLightHelper : Object3D {
    constructor(light: DirectionalLight, size: Number = definedExternally, color: Color = definedExternally)
    constructor(light: DirectionalLight, size: Number = definedExternally, color: String = definedExternally)
    constructor(light: DirectionalLight, size: Number = definedExternally, color: Number = definedExternally)
    open var light: DirectionalLight
    open var lightPlane: Line<dynamic, dynamic>
    open var targetLine: Line<dynamic, dynamic>
    open var color: dynamic /* Color? | String? | Number? */
    override var matrix: Matrix4
    override var matrixAutoUpdate: Boolean
    open fun dispose()
    open fun update()
}