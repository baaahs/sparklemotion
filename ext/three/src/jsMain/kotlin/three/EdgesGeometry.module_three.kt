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

external interface `T$77`<TBufferGeometry> {
    val geometry: TBufferGeometry?
    val thresholdAngle: Number
}

open external class EdgesGeometry<TBufferGeometry : BufferGeometry<NormalBufferAttributes>>(geometry: TBufferGeometry? = definedExternally, thresholdAngle: Number = definedExternally) : BufferGeometry<NormalBufferAttributes> {
    open var override: Any
    override val type: String /* String | "EdgesGeometry" */
    open val parameters: `T$77`<Any?>
}