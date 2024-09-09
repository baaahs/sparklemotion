@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

import org.khronos.webgl.WebGLRenderingContext

external interface `T$13` {
    var geometries: Number
    var textures: Number
}

external interface `T$14` {
    var calls: Number
    var frame: Number
    var lines: Number
    var points: Number
    var triangles: Number
}

open external class WebGLInfo(gl: WebGLRenderingContext) {
    open var autoReset: Boolean
    open var memory: `T$13`
    open var programs: Array<WebGLProgram>?
    open var render: `T$14`
    open fun update(count: Number, mode: GLenum, instanceCount: Number)
    open fun reset()
}