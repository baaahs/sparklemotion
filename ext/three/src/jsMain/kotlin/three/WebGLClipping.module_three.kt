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

external interface `T$64` {
    var value: Any
    var needsUpdate: Boolean
}

open external class WebGLClipping(properties: WebGLProperties) {
    open var uniform: `T$64`
    open var numPlanes: Number
    open var numIntersection: Number
    open fun init(planes: Array<Any>, enableLocalClipping: Boolean): Boolean
    open fun beginShadows()
    open fun endShadows()
    open fun setGlobalState(planes: Array<Plane>, camera: Camera)
    open fun setState(material: Material, camera: Camera, useCache: Boolean)
}