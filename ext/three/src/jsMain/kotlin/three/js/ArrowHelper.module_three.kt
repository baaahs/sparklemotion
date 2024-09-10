package three.js

open external class ArrowHelper : Object3D__0 {
    constructor(dir: Vector3 = definedExternally, origin: Vector3 = definedExternally, length: Number = definedExternally, color: Color = definedExternally, headLength: Number = definedExternally, headWidth: Number = definedExternally)
    constructor()
    constructor(dir: Vector3 = definedExternally)
    constructor(dir: Vector3 = definedExternally, origin: Vector3 = definedExternally)
    constructor(dir: Vector3 = definedExternally, origin: Vector3 = definedExternally, length: Number = definedExternally)
    constructor(dir: Vector3 = definedExternally, origin: Vector3 = definedExternally, length: Number = definedExternally, color: Color = definedExternally)
    constructor(dir: Vector3 = definedExternally, origin: Vector3 = definedExternally, length: Number = definedExternally, color: Color = definedExternally, headLength: Number = definedExternally)
    constructor(dir: Vector3 = definedExternally, origin: Vector3 = definedExternally, length: Number = definedExternally, color: String = definedExternally, headLength: Number = definedExternally, headWidth: Number = definedExternally)
    constructor(dir: Vector3 = definedExternally, origin: Vector3 = definedExternally, length: Number = definedExternally, color: String = definedExternally)
    constructor(dir: Vector3 = definedExternally, origin: Vector3 = definedExternally, length: Number = definedExternally, color: String = definedExternally, headLength: Number = definedExternally)
    constructor(dir: Vector3 = definedExternally, origin: Vector3 = definedExternally, length: Number = definedExternally, color: Number = definedExternally, headLength: Number = definedExternally, headWidth: Number = definedExternally)
    constructor(dir: Vector3 = definedExternally, origin: Vector3 = definedExternally, length: Number = definedExternally, color: Number = definedExternally)
    constructor(dir: Vector3 = definedExternally, origin: Vector3 = definedExternally, length: Number = definedExternally, color: Number = definedExternally, headLength: Number = definedExternally)
    open var override: Any
    override val type: String /* String | "ArrowHelper" */
    open var line: Line__0
    open var cone: Mesh__0
    open fun setColor(color: Color)
    open fun setColor(color: String)
    open fun setColor(color: Number)
    open fun setDirection(dir: Vector3)
    open fun setLength(length: Number, headLength: Number = definedExternally, headWidth: Number = definedExternally)
    open fun copy(source: ArrowHelper /* this */): ArrowHelper /* this */
    open fun dispose()
}