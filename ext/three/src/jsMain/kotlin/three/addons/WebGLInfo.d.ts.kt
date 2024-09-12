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

external interface `T$23` {
    var geometries: Number
    var textures: Number
}

external interface `T$24` {
    var calls: Number
    var frame: Number
    var lines: Number
    var points: Number
    var triangles: Number
}

external open class WebGLInfo(gl: WebGLRenderingContext) {
    open var autoReset: Boolean
    open var memory: `T$23`
    open var programs: Array<WebGLProgram>?
    open var render: `T$24`
    open fun update(count: Number, mode: Number, instanceCount: Number)
    open fun reset()
}