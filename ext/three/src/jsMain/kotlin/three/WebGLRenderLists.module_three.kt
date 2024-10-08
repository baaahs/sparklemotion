@file:JsModule("three")
@file:JsNonModule
package three

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
    var `object`: Object3D/*<Object3DEventMap>*/
    var geometry: BufferGeometry<NormalBufferAttributes>?
    var material: Material
    var program: WebGLProgram
    var groupOrder: Number
    var renderOrder: Number
    var z: Number
    var group: Object3D/*<Object3DEventMap>*/?
}

open external class WebGLRenderList(properties: WebGLProperties) {
    open var opaque: Array<RenderItem>
    open var transparent: Array<RenderItem>
    open var transmissive: Array<RenderItem>
    open fun init()
    open fun push(obj: Object3D/*<Object3DEventMap>*/, geometry: BufferGeometry<NormalBufferAttributes>?, material: Material, groupOrder: Number, z: Number, group: Object3D/*<Object3DEventMap>*/?)
    open fun unshift(obj: Object3D/*<Object3DEventMap>*/, geometry: BufferGeometry<NormalBufferAttributes>?, material: Material, groupOrder: Number, z: Number, group: Object3D/*<Object3DEventMap>*/?)
    open fun sort(opaqueSort: (a: Any, b: Any) -> Number, transparentSort: (a: Any, b: Any) -> Number)
    open fun finish()
}

open external class WebGLRenderLists(properties: WebGLProperties) {
    open fun dispose()
    open fun get(scene: Scene, renderCallDepth: Number): WebGLRenderList
}