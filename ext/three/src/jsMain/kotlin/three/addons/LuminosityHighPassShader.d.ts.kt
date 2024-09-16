@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.IUniform__0

external interface `T$134` {
    var tDiffuse: IUniform__0
    var luminosityThreshold: IUniform__0
    var smoothWidth: IUniform__0
    var defaultColor: IUniform__0
    var defaultOpacity: IUniform__0
}

external object LuminosityHighPassShader {
    var name: String
    var shaderID: String
    var uniforms: `T$134`
    var vertexShader: String
    var fragmentShader: String
}