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

open external class PointLightShadow(camera: PerspectiveCamera) : LightShadow<PerspectiveCamera> {
    open val isPointLightShadow: Any = definedExternally /* true */
    open var override: Any
    open fun updateMatrices(light: Light__0, viewportIndex: Number = definedExternally)
}