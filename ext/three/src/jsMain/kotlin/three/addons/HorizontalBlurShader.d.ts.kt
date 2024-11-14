@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.IUniform__0

external interface `T$130` {
    var tDiffuse: IUniform__0
    var h: IUniform__0
}

external object HorizontalBlurShader {
    var uniforms: `T$130`
    var vertexShader: String
    var fragmentShader: String
}