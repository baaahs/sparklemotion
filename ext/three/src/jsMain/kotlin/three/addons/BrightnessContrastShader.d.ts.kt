@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.IUniform__0

external interface `T$104` {
    var tDiffuse: IUniform__0
    var brightness: IUniform__0
    var contrast: IUniform__0
}

external object BrightnessContrastShader {
    var name: String
    var uniforms: `T$104`
    var vertexShader: String
    var fragmentShader: String
}