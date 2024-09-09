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

external open class CubeCamera(near: Number, far: Number, renderTarget: WebGLCubeRenderTarget) : Object3D__0 {
    open var override: Any
    override val type: String /* String | "CubeCamera" */
    open var renderTarget: WebGLCubeRenderTarget
    open var coordinateSystem: Any
    open var activeMipmapLevel: Number
    open fun updateCoordinateSystem()
    open fun update(renderer: WebGLRenderer, scene: Object3D__0)
}