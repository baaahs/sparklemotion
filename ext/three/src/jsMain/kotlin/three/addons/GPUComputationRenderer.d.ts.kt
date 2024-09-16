@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.*

external interface Variable {
    var name: String
    var initialValueTexture: Texture
    var material: ShaderMaterial
    var dependencies: Array<Variable>
    var renderTargets: Array<WebGLRenderTarget<*>>
    var wrapS: Number
    var wrapT: Number
    var minFilter: Number
    var magFilter: Number
}

open external class GPUComputationRenderer(sizeX: Number, sizeY: Number, renderer: WebGLRenderer) {
    open fun setDataType(type: Any)
    open fun addVariable(variableName: String, computeFragmentShader: String, initialValueTexture: Texture): Variable
    open fun setVariableDependencies(variable: Variable, dependencies: Array<Variable>?)
    open fun init(): String?
    open fun compute()
    open fun getCurrentRenderTarget(variable: Variable): WebGLRenderTarget<*>
    open fun getAlternateRenderTarget(variable: Variable): WebGLRenderTarget<*>
    open fun addResolutionDefine(materialShader: ShaderMaterial)
    open fun createShaderMaterial(computeFragmentShader: String, uniforms: `T$17` = definedExternally): ShaderMaterial
    open fun createRenderTarget(sizeXTexture: Number, sizeYTexture: Number, wrapS: Any, wrapT: Number, minFilter: Any, magFilter: Any): WebGLRenderTarget<*>
    open fun createTexture(): DataTexture
    open fun renderTexture(input: Texture, output: WebGLRenderTarget<*>)
    open fun doRenderTarget(material: Material, output: WebGLRenderTarget<*>)
    open fun dispose()
}