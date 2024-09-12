@file:JsModule("three")
@file:JsNonModule
package three

import org.khronos.webgl.WebGLRenderingContext

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

open external class WebGLInfo(gl: WebGLRenderingContext) {
    open var autoReset: Boolean
    open var memory: `T$23`
    open var programs: Array<WebGLProgram>?
    open var render: `T$24`
    open fun update(count: Number, mode: Number, instanceCount: Number)
    open fun reset()
}