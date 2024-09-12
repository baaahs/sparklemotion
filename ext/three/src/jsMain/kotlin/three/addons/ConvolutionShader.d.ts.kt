@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.IUniform__0

external interface `T$107` {
    var KERNEL_SIZE_FLOAT: String
    var KERNEL_SIZE_INT: String
}

external interface `T$108` {
    var tDiffuse: IUniform__0
    var uImageIncrement: IUniform__0
    var cKernel: IUniform__0
}

external object ConvolutionShader {
    var defines: `T$107`
    var uniforms: `T$108`
    var vertexShader: String
    var fragmentShader: String
    fun buildKernel(sigma: Number): Array<Number>
}