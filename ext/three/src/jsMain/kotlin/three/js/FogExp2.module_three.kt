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

external interface FogExp2JSON {
    var type: String
    var name: String
    var color: Number
    var density: Number
}

open external class FogExp2 {
    constructor(color: Color, density: Number = definedExternally)
    constructor(color: Color)
    constructor(color: String, density: Number = definedExternally)
    constructor(color: String)
    constructor(color: Number, density: Number = definedExternally)
    constructor(color: Number)
    open val isFogExp2: Boolean
    open var name: String
    open var color: Color
    open var density: Number
    open fun clone(): FogExp2
    open fun toJSON(): FogExp2JSON
}