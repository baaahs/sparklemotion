@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
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

external open class WebGLIndexedBufferRenderer(gl: WebGLRenderingContext, extensions: Any, info: Any) {
    open var setMode: (value: Any) -> Unit
    open var setIndex: (index: Any) -> Unit
    open var render: (start: Any, count: Number) -> Unit
    open var renderInstances: (start: Any, count: Number, primcount: Number) -> Unit
    open var renderMultiDraw: (starts: Int32Array, counts: Int32Array, drawCount: Number) -> Unit
    open var renderMultiDrawInstances: (starts: Int32Array, counts: Int32Array, drawCount: Number, primcount: Int32Array) -> Unit
}