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

external interface SpriteMaterialParameters : MaterialParameters {
    var color: dynamic /* Color? | String? | Number? */
        get() = definedExternally
        set(value) = definedExternally
    var map: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var alphaMap: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var rotation: Number?
        get() = definedExternally
        set(value) = definedExternally
    var sizeAttenuation: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var fog: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

open external class SpriteMaterial(parameters: SpriteMaterialParameters = definedExternally) : Material {
    open val isSpriteMaterial: Boolean
    override var type: String
    open var color: Color
    open var map: Texture?
    open var alphaMap: Texture?
    open var rotation: Number
    open var sizeAttenuation: Boolean
    override var transparent: Boolean
    open var fog: Boolean
    open fun setValues(parameters: SpriteMaterialParameters)
    override fun setValues(values: MaterialParameters)
    open fun copy(source: SpriteMaterial): SpriteMaterial /* this */
    override fun copy(material: Material): Material /* this */
}