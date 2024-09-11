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

open external class HemisphereLightHelper : Object3D/*<Object3DEventMap>*/ {
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