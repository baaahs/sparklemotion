@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

import org.khronos.webgl.WebGLRenderingContext

open external class WebGLGeometries(gl: WebGLRenderingContext, attributes: WebGLAttributes, info: WebGLInfo) {
    open fun get(obj: Object3D, geometry: Geometry): BufferGeometry
    open fun get(obj: Object3D, geometry: BufferGeometry): BufferGeometry
    open fun update(geometry: Geometry)
    open fun update(geometry: BufferGeometry)
    open fun getWireframeAttribute(geometry: Geometry): BufferAttribute
    open fun getWireframeAttribute(geometry: BufferGeometry): BufferAttribute
}