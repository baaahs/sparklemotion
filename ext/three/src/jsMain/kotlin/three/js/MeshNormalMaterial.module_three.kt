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

external interface MeshNormalMaterialParameters : MaterialParameters {
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
    var wireframe: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var wireframeLinewidth: Number?
        get() = definedExternally
        set(value) = definedExternally
    var flatShading: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external open class MeshNormalMaterial(parameters: MeshNormalMaterialParameters = definedExternally) : Material {
    open val isMeshNormalMaterial: Boolean
    override var type: String
    open var bumpMap: Texture?
    open var bumpScale: Number
    open var normalMap: Texture?
    open var normalMapType: Any
    open var normalScale: Vector2
    open var displacementMap: Texture?
    open var displacementScale: Number
    open var displacementBias: Number
    open var wireframe: Boolean
    open var wireframeLinewidth: Number
    open var flatShading: Boolean
    open fun setValues(parameters: MeshNormalMaterialParameters)
    override fun setValues(values: MaterialParameters)
}