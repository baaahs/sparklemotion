@file:JsModule("three")
@file:JsNonModule
package three

import js.objects.Record
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

external interface `T$95` {
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
    var extensions: `T$95`?
        get() = definedExternally
        set(value) = definedExternally
    var glslVersion: Any?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ShaderMaterialJSON : MaterialJSON {
    var glslVersion: Number?
    var uniforms: Record<String, dynamic /* `T$97` | `T$98` | `T$99` | `T$100` | `T$101` | `T$102` | `T$103` | `T$104` */>
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

external interface `T$96` {
    var clipCullDistance: Boolean
    var multiDraw: Boolean
}

open external class ShaderMaterial(parameters: ShaderMaterialParameters = definedExternally) : Material {
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
    open var extensions: `T$96`
    open var defaultAttributeValues: Any
    open var index0AttributeName: String?
    open var uniformsNeedUpdate: Boolean
    open var glslVersion: Any?
    open fun setValues(parameters: ShaderMaterialParameters)
    override fun setValues(values: MaterialParameters)
    override fun toJSON(meta: JSONMeta): ShaderMaterialJSON
}