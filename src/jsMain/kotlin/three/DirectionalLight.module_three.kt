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

open external class DirectionalLight : Light {
    constructor(color: Color = definedExternally, intensity: Number = definedExternally)
    constructor(color: String = definedExternally, intensity: Number = definedExternally)
    constructor(color: Number = definedExternally, intensity: Number = definedExternally)
    override var type: String
    override var position: Vector3
    open var target: Object3D
    override var intensity: Number
    override var shadow: DirectionalLightShadow
    open var isDirectionalLight: Boolean
}