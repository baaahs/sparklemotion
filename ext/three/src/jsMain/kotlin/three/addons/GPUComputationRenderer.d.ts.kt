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

external interface Variable {
    var name: String
    var initialValueTexture: Texture
    var material: ShaderMaterial
    var dependencies: Array<Variable>
    var renderTargets: Array<WebGLRenderTarget__0>
    var wrapS: Number
    var wrapT: Number
    var minFilter: Number
    var magFilter: Number
}

external open class GPUComputationRenderer(sizeX: Number, sizeY: Number, renderer: WebGLRenderer) {
    open fun setDataType(type: Any)
    open fun addVariable(variableName: String, computeFragmentShader: String, initialValueTexture: Texture): Variable
    open fun setVariableDependencies(variable: Variable, dependencies: Array<Variable>?)
    open fun init(): String?
    open fun compute()
    open fun getCurrentRenderTarget(variable: Variable): WebGLRenderTarget__0
    open fun getAlternateRenderTarget(variable: Variable): WebGLRenderTarget__0
    open fun addResolutionDefine(materialShader: ShaderMaterial)
    open fun createShaderMaterial(computeFragmentShader: String, uniforms: `T$17` = definedExternally): ShaderMaterial
    open fun createRenderTarget(sizeXTexture: Number, sizeYTexture: Number, wrapS: Any, wrapT: Number, minFilter: Any, magFilter: Any): WebGLRenderTarget__0
    open fun createTexture(): DataTexture
    open fun renderTexture(input: Texture, output: WebGLRenderTarget__0)
    open fun doRenderTarget(material: Material, output: WebGLRenderTarget__0)
    open fun dispose()
}