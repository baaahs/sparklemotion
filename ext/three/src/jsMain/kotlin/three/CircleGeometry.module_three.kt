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

external interface `T$73` {
    val radius: Number
    val segments: Number
    val thetaStart: Number
    val thetaLength: Number
}

open external class CircleGeometry(radius: Number = definedExternally, segments: Number = definedExternally, thetaStart: Number = definedExternally, thetaLength: Number = definedExternally) : BufferGeometry<NormalBufferAttributes> {
    open var override: Any
    override val type: String /* String | "CircleGeometry" */
    open val parameters: `T$73`

    companion object {
        fun fromJSON(data: Any): CircleGeometry
    }
}