@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

external interface `T$8` {
    @nativeGetter
    operator fun get(uniform: String): IUniform?
    @nativeSetter
    operator fun set(uniform: String, value: IUniform)
}

external interface Shader {
    var uniforms: `T$8`
    var vertexShader: String
    var fragmentShader: String
}