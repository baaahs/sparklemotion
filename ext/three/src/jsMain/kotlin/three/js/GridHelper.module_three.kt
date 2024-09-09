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

external open class GridHelper : LineSegments__2<BufferGeometry__0, LineBasicMaterial> {
    constructor(size: Number = definedExternally, divisions: Number = definedExternally, color1: Color = definedExternally, color2: Color = definedExternally)
    constructor()
    constructor(size: Number = definedExternally)
    constructor(size: Number = definedExternally, divisions: Number = definedExternally)
    constructor(size: Number = definedExternally, divisions: Number = definedExternally, color1: Color = definedExternally)
    constructor(size: Number = definedExternally, divisions: Number = definedExternally, color1: Color = definedExternally, color2: String = definedExternally)
    constructor(size: Number = definedExternally, divisions: Number = definedExternally, color1: Color = definedExternally, color2: Number = definedExternally)
    constructor(size: Number = definedExternally, divisions: Number = definedExternally, color1: String = definedExternally, color2: Color = definedExternally)
    constructor(size: Number = definedExternally, divisions: Number = definedExternally, color1: String = definedExternally)
    constructor(size: Number = definedExternally, divisions: Number = definedExternally, color1: String = definedExternally, color2: String = definedExternally)
    constructor(size: Number = definedExternally, divisions: Number = definedExternally, color1: String = definedExternally, color2: Number = definedExternally)
    constructor(size: Number = definedExternally, divisions: Number = definedExternally, color1: Number = definedExternally, color2: Color = definedExternally)
    constructor(size: Number = definedExternally, divisions: Number = definedExternally, color1: Number = definedExternally)
    constructor(size: Number = definedExternally, divisions: Number = definedExternally, color1: Number = definedExternally, color2: String = definedExternally)
    constructor(size: Number = definedExternally, divisions: Number = definedExternally, color1: Number = definedExternally, color2: Number = definedExternally)
    override var override: Any
    override val type: String /* String | "GridHelper" */
    open fun dispose()
}