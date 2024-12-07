package three.addons

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
import three.*
import kotlin.js.*

open external class RectAreaLightHelper : Line<BufferGeometry<NormalOrGLBufferAttributes>, Material> {
    constructor(light: RectAreaLight, color: Color = definedExternally)
    constructor(light: RectAreaLight)
    constructor(light: RectAreaLight, color: String = definedExternally)
    constructor(light: RectAreaLight, color: Number = definedExternally)
    open var light: RectAreaLight
    open var color: dynamic /* Color? | String? | Number? */
    open fun dispose()
}