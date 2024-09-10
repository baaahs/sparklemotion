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

external interface `T$86` {
    val path: Curve<Vector3>
    val tubularSegments: Number
    val radius: Number
    val radialSegments: Number
    val closed: Boolean
}

open external class TubeGeometry(path: Curve<Vector3> = definedExternally, tubularSegments: Number = definedExternally, radius: Number = definedExternally, radialSegments: Number = definedExternally, closed: Boolean = definedExternally) : BufferGeometry<NormalBufferAttributes> {
    open var override: Any
    override val type: String /* String | "TubeGeometry" */
    open val parameters: `T$86`
    open var tangents: Array<Vector3>
    open var normals: Array<Vector3>
    open var binormals: Array<Vector3>

    companion object {
        fun fromJSON(data: Any): TubeGeometry
    }
}