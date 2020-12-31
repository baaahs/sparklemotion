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

open external class LightShadow(camera: Camera) {
    open var camera: Camera
    open var bias: Number
    open var normalBias: Number
    open var radius: Number
    open var mapSize: Vector2
    open var map: RenderTarget
    open var mapPass: RenderTarget
    open var matrix: Matrix4
    open var autoUpdate: Boolean
    open var needsUpdate: Boolean
    open fun copy(source: LightShadow): LightShadow /* this */
    open fun clone(recursive: Boolean = definedExternally): LightShadow /* this */
    open fun toJSON(): Any
    open fun getFrustum(): Number
    open fun updateMatrices(light: Light, viewportIndex: Number = definedExternally)
    open fun getViewport(viewportIndex: Number): Vector4
    open fun getFrameExtents(): Vector2
}