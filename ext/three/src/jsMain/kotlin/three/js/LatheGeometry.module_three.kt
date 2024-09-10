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

external interface `T$79` {
    val points: Array<Vector2>
    val segments: Number
    val phiStart: Number
    val phiLength: Number
}

open external class LatheGeometry(points: Array<Vector2> = definedExternally, segments: Number = definedExternally, phiStart: Number = definedExternally, phiLength: Number = definedExternally) : BufferGeometry__0 {
    open var override: Any
    override val type: String /* String | "LatheGeometry" */
    open val parameters: `T$79`

    companion object {
        fun fromJSON(data: Any): LatheGeometry
    }
}