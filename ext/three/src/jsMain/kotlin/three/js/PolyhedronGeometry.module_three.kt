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

external interface `T$76` {
    val vertices: Array<Number>
    val indices: Array<Number>
    val radius: Number
    val detail: Number
}

open external class PolyhedronGeometry(vertices: Array<Number> = definedExternally, indices: Array<Number> = definedExternally, radius: Number = definedExternally, detail: Number = definedExternally) : BufferGeometry__0 {
    open var override: Any
    override val type: String /* String | "PolyhedronGeometry" */
    open val parameters: `T$76`

    companion object {
        fun fromJSON(data: Any): PolyhedronGeometry
    }
}