@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

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

external interface FogJSON {
    var type: String
    var name: String
    var color: Number
    var near: Number
    var far: Number
}

external open class Fog {
    constructor(color: Color, near: Number = definedExternally, far: Number = definedExternally)
    constructor(color: Color)
    constructor(color: Color, near: Number = definedExternally)
    constructor(color: String, near: Number = definedExternally, far: Number = definedExternally)
    constructor(color: String)
    constructor(color: String, near: Number = definedExternally)
    constructor(color: Number, near: Number = definedExternally, far: Number = definedExternally)
    constructor(color: Number)
    constructor(color: Number, near: Number = definedExternally)
    open val isFog: Boolean
    open var name: String
    open var color: Color
    open var near: Number
    open var far: Number
    open fun clone(): Fog
    open fun toJSON(): FogJSON
}