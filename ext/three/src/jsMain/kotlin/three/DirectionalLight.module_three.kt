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

open external class DirectionalLight : Light<DirectionalLightShadow> {
    constructor(color: Color = definedExternally, intensity: Number = definedExternally)
    constructor()
    constructor(color: Color = definedExternally)
    constructor(color: String = definedExternally, intensity: Number = definedExternally)
    constructor(color: String = definedExternally)
    constructor(color: Number = definedExternally, intensity: Number = definedExternally)
    constructor(color: Number = definedExternally)
    open val isDirectionalLight: Boolean
    override var override: Any
    override val type: String /* String | "DirectionalLight" */
    override var castShadow: Boolean
    override val position: Vector3
    override var shadow: DirectionalLightShadow
    open var target: Object3D/*<Object3DEventMap>*/
    override fun dispose()
}