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

open external class BoxHelper : LineSegments__2<BufferGeometry__0, LineBasicMaterial> {
    constructor(obj: Object3D__0, color: Color = definedExternally)
    constructor(obj: Object3D__0)
    constructor(obj: Object3D__0, color: String = definedExternally)
    constructor(obj: Object3D__0, color: Number = definedExternally)
    override var override: Any
    override val type: String /* String | "BoxHelper" */
    open fun update(obj: Object3D__0 = definedExternally)
    open fun setFromObject(obj: Object3D__0): BoxHelper /* this */
    open fun dispose()
}