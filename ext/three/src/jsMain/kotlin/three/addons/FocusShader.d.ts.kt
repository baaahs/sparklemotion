@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.IUniform__0

external interface `T$116` {
    var tDiffuse: IUniform__0
    var screenWidth: IUniform__0
    var screenHeight: IUniform__0
    var sampleDistance: IUniform__0
    var waveFactor: IUniform__0
}

external object FocusShader {
    var name: String
    var uniforms: `T$116`
    var vertexShader: String
    var fragmentShader: String
}