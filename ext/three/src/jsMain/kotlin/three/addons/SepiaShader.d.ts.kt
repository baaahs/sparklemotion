@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.IUniform__0

external interface `T$142` {
    var tDiffuse: IUniform__0
    var amount: IUniform__0
}

external object SepiaShader {
    var uniforms: `T$142`
    var vertexShader: String
    var fragmentShader: String
}