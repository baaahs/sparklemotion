@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

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

external interface MeshDepthMaterialParameters : MaterialParameters {
    var map: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var alphaMap: Texture?
        get() = definedExternally
        set(value) = definedExternally
    var depthPacking: Any?
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
}

external open class MeshDepthMaterial(parameters: MeshDepthMaterialParameters = definedExternally) : Material {
    open val isMeshDepthMaterial: Boolean
    override var type: String
    open var map: Texture?
    open var alphaMap: Texture?
    open var depthPacking: Any
    open var displacementMap: Texture?
    open var displacementScale: Number
    open var displacementBias: Number
    open var wireframe: Boolean
    open var wireframeLinewidth: Number
    open var fog: Boolean
    open fun setValues(parameters: MeshDepthMaterialParameters)
    override fun setValues(values: MaterialParameters)
}