@file:JsModule("three")
@file:JsNonModule
package three

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

open external class DirectionalLightHelper : Object3D/*<Object3DEventMap>*/ {
    constructor(light: DirectionalLight, size: Number = definedExternally, color: Color = definedExternally)
    constructor(light: DirectionalLight)
    constructor(light: DirectionalLight, size: Number = definedExternally)
    constructor(light: DirectionalLight, size: Number = definedExternally, color: String = definedExternally)
    constructor(light: DirectionalLight, size: Number = definedExternally, color: Number = definedExternally)
    open var override: Any
    override val type: String /* String | "DirectionalLightHelper" */
    open var lightPlane: Line<*, *>
    open var light: DirectionalLight
    override var matrix: Matrix4
    override var matrixAutoUpdate: Boolean
    open var color: dynamic /* Color? | String? | Number? */
    open var targetLine: Line<*, *>
    open fun update()
    open fun dispose()
}