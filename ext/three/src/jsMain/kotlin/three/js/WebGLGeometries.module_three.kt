@file:JsModule("three")
@file:JsNonModule
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

open external class WebGLGeometries(gl: WebGLRenderingContext, attributes: WebGLAttributes, info: WebGLInfo) {
    open fun get(obj: Object3D/*<Object3DEventMap>*/, geometry: BufferGeometry<NormalBufferAttributes>): BufferGeometry<NormalBufferAttributes>
    open fun update(geometry: BufferGeometry<NormalBufferAttributes>)
    open fun getWireframeAttribute(geometry: BufferGeometry<NormalBufferAttributes>): BufferAttribute
}