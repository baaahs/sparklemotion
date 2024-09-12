@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.IUniform__0

external interface `T$157` {
    var texture: IUniform__0
    var delta: IUniform__0
}

external object TriangleBlurShader {
    var name: String
    var uniforms: `T$157`
    var vertexShader: String
    var fragmentShader: String
}