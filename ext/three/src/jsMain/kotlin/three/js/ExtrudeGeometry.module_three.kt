@file:JsModule("three")
@file:JsNonModule
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

external interface ExtrudeGeometryOptions {
    var curveSegments: Number?
        get() = definedExternally
        set(value) = definedExternally
    var steps: Number?
        get() = definedExternally
        set(value) = definedExternally
    var depth: Number?
        get() = definedExternally
        set(value) = definedExternally
    var bevelEnabled: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var bevelThickness: Number?
        get() = definedExternally
        set(value) = definedExternally
    var bevelSize: Number?
        get() = definedExternally
        set(value) = definedExternally
    var bevelOffset: Number?
        get() = definedExternally
        set(value) = definedExternally
    var bevelSegments: Number?
        get() = definedExternally
        set(value) = definedExternally
    var extrudePath: Curve<Vector3>?
        get() = definedExternally
        set(value) = definedExternally
    var UVGenerator: UVGenerator?
        get() = definedExternally
        set(value) = definedExternally
}

external interface UVGenerator {
    fun generateTopUV(geometry: ExtrudeGeometry, vertices: Array<Number>, indexA: Number, indexB: Number, indexC: Number): Array<Vector2>
    fun generateSideWallUV(geometry: ExtrudeGeometry, vertices: Array<Number>, indexA: Number, indexB: Number, indexC: Number, indexD: Number): Array<Vector2>
}

external interface `T$78` {
    val shapes: dynamic /* Shape | Array<Shape> */
        get() = definedExternally
    val options: ExtrudeGeometryOptions
}

open external class ExtrudeGeometry : BufferGeometry<NormalBufferAttributes> {
    constructor(shapes: Shape = definedExternally, options: ExtrudeGeometryOptions = definedExternally)
    constructor()
    constructor(shapes: Shape = definedExternally)
    constructor(shapes: Array<Shape> = definedExternally, options: ExtrudeGeometryOptions = definedExternally)
    constructor(shapes: Array<Shape> = definedExternally)
    open var override: Any
    override val type: String /* String | "ExtrudeGeometry" */
    open val parameters: `T$78`
    open fun addShape(shape: Shape)

    companion object {
        fun fromJSON(data: Any, shapes: Any): ExtrudeGeometry
    }
}