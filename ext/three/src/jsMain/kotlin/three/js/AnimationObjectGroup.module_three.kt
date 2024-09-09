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

external interface `T$68` {
    var total: Number
    var inUse: Number
}

external interface `T$69` {
    var bindingsPerObject: Number
    var objects: `T$68`
}

external open class AnimationObjectGroup(vararg args: Any) {
    open var uuid: String
    open var stats: `T$69`
    open val isAnimationObjectGroup: Boolean
    open fun add(vararg args: Any)
    open fun remove(vararg args: Any)
    open fun uncache(vararg args: Any)
}