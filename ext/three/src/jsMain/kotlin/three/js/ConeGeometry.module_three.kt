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

open external class ConeGeometry(radius: Number = definedExternally, height: Number = definedExternally, radialSegments: Number = definedExternally, heightSegments: Number = definedExternally, openEnded: Boolean = definedExternally, thetaStart: Number = definedExternally, thetaLength: Number = definedExternally) : CylinderGeometry {
    override var override: Any
    override val type: String /* String | "ConeGeometry" */

    companion object {
        fun fromJSON(data: Any): ConeGeometry
    }
}