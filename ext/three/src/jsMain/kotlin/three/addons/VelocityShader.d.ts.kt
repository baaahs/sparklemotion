@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

external object VelocityShader {
    var name: String
    var uniforms: Any
    var fragmentShader: String
    var vertexShader: String
}