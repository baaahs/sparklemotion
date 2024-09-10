package three.js

import kotlin.js.*
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*

external interface RenderItem {
    var id: Number
    var `object`: Object3D__0
    var geometry: BufferGeometry__0?
    var material: Material
    var program: WebGLProgram
    var groupOrder: Number
    var renderOrder: Number
    var z: Number
    var group: Group__0?
}

open external class WebGLRenderList(properties: WebGLProperties) {
    open var opaque: Array<RenderItem>
    open var transparent: Array<RenderItem>
    open var transmissive: Array<RenderItem>
    open fun init()
    open fun push(obj: Object3D__0, geometry: BufferGeometry__0?, material: Material, groupOrder: Number, z: Number, group: Group__0?)
    open fun unshift(obj: Object3D__0, geometry: BufferGeometry__0?, material: Material, groupOrder: Number, z: Number, group: Group__0?)
    open fun sort(opaqueSort: (a: Any, b: Any) -> Number, transparentSort: (a: Any, b: Any) -> Number)
    open fun finish()
}

open external class WebGLRenderLists(properties: WebGLProperties) {
    open fun dispose()
    open fun get(scene: Scene, renderCallDepth: Number): WebGLRenderList
}