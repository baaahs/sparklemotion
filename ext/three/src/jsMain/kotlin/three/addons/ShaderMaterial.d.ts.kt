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

external interface `T$45` {
    var clipCullDistance: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var multiDraw: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ShaderMaterialParameters : MaterialParameters {
    var uniforms: `T$17`?
        get() = definedExternally
        set(value) = definedExternally
    var uniformsGroups: Array<UniformsGroup>?
        get() = definedExternally
        set(value) = definedExternally
    var vertexShader: String?
        get() = definedExternally
        set(value) = definedExternally
    var fragmentShader: String?
        get() = definedExternally
        set(value) = definedExternally
    var linewidth: Number?
        get() = definedExternally
        set(value) = definedExternally
    var wireframe: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var wireframeLinewidth: Number?
        get() = definedExternally
        set(value) = definedExternally
    var lights: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var clipping: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var fog: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var extensions: `T$45`?
        get() = definedExternally
        set(value) = definedExternally
    var glslVersion: Any?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `T$54` {
    var value: Any
}

external interface ShaderMaterialJSON : MaterialJSON {
    var glslVersion: Number?
    var uniforms: Record<String, dynamic /* `T$47` | `T$48` | `T$49` | `T$50` | `T$51` | `T$52` | `T$53` | `T$54` */>
    var defines: Record<String, Any>?
        get() = definedExternally
        set(value) = definedExternally
    var vertexShader: String
    var ragmentShader: String
    var lights: Boolean
    var clipping: Boolean
    var extensions: Record<String, Boolean>?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `T$46` {
    var clipCullDistance: Boolean
    var multiDraw: Boolean
}

external open class ShaderMaterial(parameters: ShaderMaterialParameters = definedExternally) : Material {
    open val isShaderMaterial: Boolean
    override var type: String
    open var uniforms: `T$17`
    open var uniformsGroups: Array<UniformsGroup>
    open var vertexShader: String
    open var fragmentShader: String
    open var linewidth: Number
    open var wireframe: Boolean
    open var wireframeLinewidth: Number
    open var fog: Boolean
    open var lights: Boolean
    open var clipping: Boolean
    open var extensions: `T$46`
    open var defaultAttributeValues: Any
    open var index0AttributeName: String?
    open var uniformsNeedUpdate: Boolean
    open var glslVersion: Any?
    open fun setValues(parameters: ShaderMaterialParameters)
    override fun setValues(values: MaterialParameters)
    override fun toJSON(meta: JSONMeta): ShaderMaterialJSON
}