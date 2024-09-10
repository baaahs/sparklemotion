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

external interface MeshMatcapMaterialParameters : MaterialParameters {
    var color: dynamic /* Color? | String? | Number? */
        get() = definedExternally
        set(value) = definedExternally
    var matcap: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var map: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var bumpMap: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var bumpScale: Number?
        get() = definedExternally
        set(value) = definedExternally
    var normalMap: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var normalMapType: Any?
        get() = definedExternally
        set(value) = definedExternally
    var normalScale: Vector2?
        get() = definedExternally
        set(value) = definedExternally
    var displacementMap: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var displacementScale: Number?
        get() = definedExternally
        set(value) = definedExternally
    var displacementBias: Number?
        get() = definedExternally
        set(value) = definedExternally
    var alphaMap: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var fog: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var flatShading: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

open external class MeshMatcapMaterial(parameters: MeshMatcapMaterialParameters = definedExternally) : Material {
    open val isMeshMatcapMaterial: Boolean
    override var type: String
    open var color: Color
    open var matcap: Texture?
    open var map: Texture?
    open var bumpMap: Texture?
    open var bumpScale: Number
    open var normalMap: Texture?
    open var normalMapType: Any
    open var normalScale: Vector2
    open var displacementMap: Texture?
    open var displacementScale: Number
    open var displacementBias: Number
    open var alphaMap: Texture?
    open var flatShading: Boolean
    open var fog: Boolean
    open fun setValues(parameters: MeshMatcapMaterialParameters)
    override fun setValues(values: MaterialParameters)
}