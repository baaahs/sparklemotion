@file:JsModule("three")
@file:JsNonModule
package three.js

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