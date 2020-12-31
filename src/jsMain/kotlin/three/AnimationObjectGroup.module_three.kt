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

external interface `T$49` {
    var total: Number
    var inUse: Number
}

external interface `T$50` {
    var bindingsPerObject: Number
    var objects: `T$49`
}

open external class AnimationObjectGroup(vararg args: Any) {
    open var uuid: String
    open var stats: `T$50`
    open var isAnimationObjectGroup: Boolean
    open fun add(vararg args: Any)
    open fun remove(vararg args: Any)
    open fun uncache(vararg args: Any)
}