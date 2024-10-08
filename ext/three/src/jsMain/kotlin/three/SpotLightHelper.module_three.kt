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

open external class SpotLightHelper : Object3D/*<Object3DEventMap>*/ {
    constructor(light: Light__0, color: Color = definedExternally)
    constructor(light: Light__0)
    constructor(light: Light__0, color: String = definedExternally)
    constructor(light: Light__0, color: Number = definedExternally)
    open var override: Any
    override val type: String /* String | "SpotLightHelper" */
    open var cone: LineSegments<BufferGeometry<NormalBufferAttributes>, *>
    open var light: Light__0
    override var matrix: Matrix4
    open var color: dynamic /* Color? | String? | Number? */
    override var matrixAutoUpdate: Boolean
    open fun update()
    open fun dispose()
}