@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.IUniform__0

external interface `T$160` {
    var tDiffuse: IUniform__0
    var v: IUniform__0
    var r: IUniform__0
}

external object VerticalTiltShiftShader {
    var name: String
    var uniforms: `T$160`
    var vertexShader: String
    var fragmentShader: String
}