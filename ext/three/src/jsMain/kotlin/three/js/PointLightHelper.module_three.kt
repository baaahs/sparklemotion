@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class PointLightHelper : Object3D {
    constructor(light: PointLight, sphereSize: Number = definedExternally, color: Color = definedExternally)
    constructor(light: PointLight, sphereSize: Number = definedExternally, color: String = definedExternally)
    constructor(light: PointLight, sphereSize: Number = definedExternally, color: Number = definedExternally)
    override var type: String
    open var light: PointLight
    open var color: dynamic /* Color? | String? | Number? */
    override var matrix: Matrix4
    override var matrixAutoUpdate: Boolean
    open fun dispose()
    open fun update()
}