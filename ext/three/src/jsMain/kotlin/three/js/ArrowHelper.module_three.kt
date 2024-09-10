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