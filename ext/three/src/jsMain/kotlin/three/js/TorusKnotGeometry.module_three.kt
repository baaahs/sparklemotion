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

external interface `T$85` {
    val radius: Number
    val tube: Number
    val tubularSegments: Number
    val radialSegments: Number
    val p: Number
    val q: Number
}

open external class TorusKnotGeometry(radius: Number = definedExternally, tube: Number = definedExternally, tubularSegments: Number = definedExternally, radialSegments: Number = definedExternally, p: Number = definedExternally, q: Number = definedExternally) : BufferGeometry__0 {
    open var override: Any
    override val type: String /* String | "TorusKnotGeometry" */
    open val parameters: `T$85`

    companion object {
        fun fromJSON(data: Any): TorusKnotGeometry
    }
}