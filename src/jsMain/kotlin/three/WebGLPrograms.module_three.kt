@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

import kotlin.js.*
import kotlin.js.Json
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

open external class WebGLPrograms(renderer: WebGLRenderer, cubemaps: WebGLCubeMaps, extensions: WebGLExtensions, capabilities: WebGLCapabilities, bindingStates: WebGLBindingStates, clipping: WebGLClipping) {
    open var programs: Array<WebGLProgram>
    open fun getParameters(material: Material, lights: Any, shadows: Array<Any?>, scene: Scene, obj: Any): Any
    open fun getProgramCacheKey(parameters: Any): String
    open fun getUniforms(material: Material): Any?
    open fun acquireProgram(parameters: Any, cacheKey: String): WebGLProgram
    open fun releaseProgram(program: WebGLProgram)
}