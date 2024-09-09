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

external open class HemisphereLightHelper : Object3D__0 {
    constructor(light: HemisphereLight, size: Number, color: Color = definedExternally)
    constructor(light: HemisphereLight, size: Number)
    constructor(light: HemisphereLight, size: Number, color: String = definedExternally)
    constructor(light: HemisphereLight, size: Number, color: Number = definedExternally)
    open var override: Any
    override val type: String /* String | "HemisphereLightHelper" */
    open var light: HemisphereLight
    override var matrix: Matrix4
    override var matrixAutoUpdate: Boolean
    open var material: MeshBasicMaterial
    open var color: dynamic /* Color? | String? | Number? */
    open fun update()
    open fun dispose()
}