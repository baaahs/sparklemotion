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

external interface `T$82` {
    val shapes: dynamic /* Shape | Array<Shape> */
        get() = definedExternally
    val curveSegments: Number
}

open external class ShapeGeometry : BufferGeometry<NormalBufferAttributes> {
    constructor(shapes: Shape = definedExternally, curveSegments: Number = definedExternally)
    constructor()
    constructor(shapes: Shape = definedExternally)
    constructor(shapes: Array<Shape> = definedExternally, curveSegments: Number = definedExternally)
    constructor(shapes: Array<Shape> = definedExternally)
    open var override: Any
    override val type: String /* String | "ShapeGeometry" */
    open val parameters: `T$82`

    companion object {
        fun fromJSON(data: Any): ShapeGeometry
    }
}