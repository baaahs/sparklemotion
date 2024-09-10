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

open external class WebGLBufferRenderer(gl: WebGLRenderingContext, extensions: WebGLExtensions, info: WebGLInfo) {
    open var setMode: (value: Any) -> Unit
    open var render: (start: Any, count: Number) -> Unit
    open var renderInstances: (start: Any, count: Number, primcount: Number) -> Unit
    open var renderMultiDraw: (starts: Int32Array, counts: Int32Array, drawCount: Number) -> Unit
    open var renderMultiDrawInstances: (starts: Int32Array, counts: Int32Array, drawCount: Number, primcount: Int32Array) -> Unit
}