@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

import org.khronos.webgl.Float32Array

open external class ImmediateRenderObject(material: Material) : Object3D {
    open var isImmediateRenderObject: Boolean
    open var material: Material
    open var hasPositions: Boolean
    open var hasNormals: Boolean
    open var hasColors: Boolean
    open var hasUvs: Boolean
    open var positionArray: Float32Array?
    open var normalArray: Float32Array?
    open var colorArray: Float32Array?
    open var uvArray: Float32Array?
    open var count: Number
    open fun render(renderCallback: Function<*>)
}