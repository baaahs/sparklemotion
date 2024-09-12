@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

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

external interface `T$33` {
    var value: Any
    var needsUpdate: Boolean
}

external open class WebGLClipping(properties: WebGLProperties) {
    open var uniform: `T$33`
    open var numPlanes: Number
    open var numIntersection: Number
    open fun init(planes: Array<Any>, enableLocalClipping: Boolean): Boolean
    open fun beginShadows()
    open fun endShadows()
    open fun setGlobalState(planes: Array<Plane>, camera: Camera)
    open fun setState(material: Material, camera: Camera, useCache: Boolean)
}