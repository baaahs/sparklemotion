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

external interface ShapeJSON : PathJSON {
    var uuid: String
    var holes: Array<PathJSON>
}

external interface `T$6` {
    var shape: Array<Vector2>
    var holes: Array<Array<Vector2>>
}

open external class Shape(points: Array<Vector2> = definedExternally) : Path {
    override var override: Any
    override val type: String /* String | "Shape" */
    open var uuid: String
    open var holes: Array<Path>
    open fun extractPoints(divisions: Number): `T$6`
    open fun getPointsHoles(divisions: Number): Array<Array<Vector2>>
    override fun toJSON(): ShapeJSON
    open fun fromJSON(json: ShapeJSON): Shape /* this */
    override fun fromJSON(json: PathJSON): Path /* this */
}