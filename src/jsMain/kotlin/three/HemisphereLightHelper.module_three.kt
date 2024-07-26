@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class HemisphereLightHelper : Object3D {
    constructor(light: HemisphereLight, size: Number, color: Color = definedExternally)
    constructor(light: HemisphereLight, size: Number, color: Number = definedExternally)
    constructor(light: HemisphereLight, size: Number, color: String = definedExternally)
    open var light: HemisphereLight
    override var matrix: Matrix4
    override var matrixAutoUpdate: Boolean
    open var material: MeshBasicMaterial
    open var color: dynamic /* Color? | String? | Number? */
    open fun dispose()
    open fun update()
}