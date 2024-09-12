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

external interface `T$75`<TBufferGeometry> {
    val geometry: TBufferGeometry?
    val thresholdAngle: Number
}

external open class EdgesGeometry<TBufferGeometry : BufferGeometry__0>(geometry: TBufferGeometry? = definedExternally, thresholdAngle: Number = definedExternally) : BufferGeometry__0 {
    open var override: Any
    override val type: String /* String | "EdgesGeometry" */
    open val parameters: `T$75`<Any?>
}

external open class EdgesGeometry__0 : EdgesGeometry<BufferGeometry__0>