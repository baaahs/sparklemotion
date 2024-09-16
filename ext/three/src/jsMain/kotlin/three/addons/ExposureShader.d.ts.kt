@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.IUniform
import three.Texture

external interface `T$114` {
    var tDiffuse: IUniform<Texture?>
    var exposure: IUniform<Number>
}

external object ExposureShader {
    var name: String /* "ExposureShader" */
    var uniforms: `T$114`
    var vertexShader: String
    var fragmentShader: String
}