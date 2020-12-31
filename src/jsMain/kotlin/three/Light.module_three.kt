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

open external class Light : Object3D {
    constructor(hex: Number = definedExternally, intensity: Number = definedExternally)
    constructor(hex: String = definedExternally, intensity: Number = definedExternally)
    override var type: String
    open var color: Color
    open var intensity: Number
    open var isLight: Boolean
    override var receiveShadow: Boolean
    open var shadow: LightShadow
    open var shadowCameraFov: Any
    open var shadowCameraLeft: Any
    open var shadowCameraRight: Any
    open var shadowCameraTop: Any
    open var shadowCameraBottom: Any
    open var shadowCameraNear: Any
    open var shadowCameraFar: Any
    open var shadowBias: Any
    open var shadowMapWidth: Any
    open var shadowMapHeight: Any
}