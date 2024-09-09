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

external interface `T$75` {
    val radiusTop: Number
    val radiusBottom: Number
    val height: Number
    val radialSegments: Number
    val heightSegments: Number
    val openEnded: Boolean
    val thetaStart: Number
    val thetaLength: Number
}

external open class CylinderGeometry(radiusTop: Number = definedExternally, radiusBottom: Number = definedExternally, height: Number = definedExternally, radialSegments: Number = definedExternally, heightSegments: Number = definedExternally, openEnded: Boolean = definedExternally, thetaStart: Number = definedExternally, thetaLength: Number = definedExternally) : BufferGeometry__0 {
    open var override: Any
    override val type: String /* String | "CylinderGeometry" */
    open val parameters: `T$75`

    companion object {
        fun fromJSON(data: Any): CylinderGeometry
    }
}