@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.IUniform__0

external interface `T$132` {
    var tDiffuse: IUniform__0
    var hue: IUniform__0
    var saturation: IUniform__0
}

external object HueSaturationShader {
    var name: String
    var uniforms: `T$132`
    var vertexShader: String
    var fragmentShader: String
}