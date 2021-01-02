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

open external class WebGLBindingStates(gl: WebGLRenderingContext, extensions: WebGLExtensions, attributes: WebGLAttributes, capabilities: WebGLCapabilities) {
    open fun setup(obj: Object3D, material: Material, program: WebGLProgram, geometry: BufferGeometry, index: BufferAttribute)
    open fun reset()
    open fun resetDefaultState()
    open fun dispose()
    open fun releaseStatesOfGeometry()
    open fun releaseStatesOfProgram()
    open fun initAttributes()
    open fun enableAttribute(attribute: Number)
    open fun disableUnusedAttributes()
}