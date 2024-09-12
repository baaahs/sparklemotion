@file:JsModule("three")
@file:JsNonModule
package three

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

external interface `T$72` {
    val radius: Number
    val length: Number
    val capSegments: Number
    val radialSegments: Number
}

open external class CapsuleGeometry(radius: Number = definedExternally, length: Number = definedExternally, capSegments: Number = definedExternally, radialSegments: Number = definedExternally) : BufferGeometry<NormalBufferAttributes> {
    open var override: Any
    override val type: String /* String | "CapsuleGeometry" */
    open val parameters: `T$72`

    companion object {
        fun fromJSON(data: Any): CapsuleGeometry
    }
}