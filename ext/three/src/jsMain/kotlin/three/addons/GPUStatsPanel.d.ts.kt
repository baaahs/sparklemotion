package three.addons

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
import three.*
import web.gl.WebGL2RenderingContext
import kotlin.js.*

open external class GPUStatsPanel /*: Panel*/ {
    constructor(context: WebGLRenderingContext, name: String = definedExternally)
    constructor(context: WebGLRenderingContext)
    constructor(context: WebGL2RenderingContext, name: String = definedExternally)
    constructor(context: WebGL2RenderingContext)
    open var context: dynamic /* WebGLRenderingContext | WebGL2RenderingContext */
    open var extension: Any
    open var maxTime: Number
    open var activeQueries: Number
    open var startQuery: () -> Unit
    open var endQuery: () -> Unit
}