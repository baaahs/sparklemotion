@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.IUniform__0

external interface `T$113` {
    var tDiffuse: IUniform__0
    var tSize: IUniform__0
    var center: IUniform__0
    var angle: IUniform__0
    var scale: IUniform__0
}

external object DotScreenShader {
    var uniforms: `T$113`
    var vertexShader: String
    var fragmentShader: String
}