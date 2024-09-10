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

external interface `T$84` {
    val radius: Number
    val tube: Number
    val radialSegments: Number
    val tubularSegments: Number
    val arc: Number
}

open external class TorusGeometry(radius: Number = definedExternally, tube: Number = definedExternally, radialSegments: Number = definedExternally, tubularSegments: Number = definedExternally, arc: Number = definedExternally) : BufferGeometry__0 {
    open var override: Any
    override val type: String /* String | "TorusGeometry" */
    open val parameters: `T$84`

    companion object {
        fun fromJSON(data: Any): TorusGeometry
    }
}