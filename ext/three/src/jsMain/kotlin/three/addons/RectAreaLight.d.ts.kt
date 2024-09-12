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

external open class RectAreaLight : Light<Nothing?> {
    constructor(color: Color = definedExternally, intensity: Number = definedExternally, width: Number = definedExternally, height: Number = definedExternally)
    constructor()
    constructor(color: Color = definedExternally)
    constructor(color: Color = definedExternally, intensity: Number = definedExternally)
    constructor(color: Color = definedExternally, intensity: Number = definedExternally, width: Number = definedExternally)
    constructor(color: String = definedExternally, intensity: Number = definedExternally, width: Number = definedExternally, height: Number = definedExternally)
    constructor(color: String = definedExternally)
    constructor(color: String = definedExternally, intensity: Number = definedExternally)
    constructor(color: String = definedExternally, intensity: Number = definedExternally, width: Number = definedExternally)
    constructor(color: Number = definedExternally, intensity: Number = definedExternally, width: Number = definedExternally, height: Number = definedExternally)
    constructor(color: Number = definedExternally)
    constructor(color: Number = definedExternally, intensity: Number = definedExternally)
    constructor(color: Number = definedExternally, intensity: Number = definedExternally, width: Number = definedExternally)
    open val isRectAreaLight: Boolean
    override var override: Any
    override val type: String /* String | "RectAreaLight" */
    open var width: Number
    open var height: Number
    override var intensity: Number
    open var power: Number
}