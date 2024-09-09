@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

external interface RenderTarget

external interface RenderItem {
    var id: Number
    var `object`: Object3D
    var geometry: BufferGeometry?
    var material: Material
    var program: WebGLProgram
    var groupOrder: Number
    var renderOrder: Number
    var z: Number
    var group: Group?
}

open external class WebGLRenderList(properties: WebGLProperties) {
    open var opaque: Array<RenderItem>
    open var transparent: Array<RenderItem>
    open fun init()
    open fun push(obj: Object3D, geometry: BufferGeometry?, material: Material, groupOrder: Number, z: Number, group: Group?)
    open fun unshift(obj: Object3D, geometry: BufferGeometry?, material: Material, groupOrder: Number, z: Number, group: Group?)
    open fun sort(opaqueSort: Function<*>, transparentSort: Function<*>)
    open fun finish()
}

open external class WebGLRenderLists(properties: WebGLProperties) {
    open fun dispose()
    open fun get(scene: Scene, camera: Camera): WebGLRenderList
}