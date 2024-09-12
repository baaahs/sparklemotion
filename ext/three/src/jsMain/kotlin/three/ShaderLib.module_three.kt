@file:JsModule("three")
@file:JsNonModule
package three

external interface ShaderLibShader {
    var uniforms: `T$17`
    var vertexShader: String
    var fragmentShader: String
}

external object ShaderLib {
    @nativeGetter
    operator fun get(name: String): ShaderLibShader?
    @nativeSetter
    operator fun set(name: String, value: ShaderLibShader)
    var basic: ShaderLibShader
    var lambert: ShaderLibShader
    var phong: ShaderLibShader
    var standard: ShaderLibShader
    var matcap: ShaderLibShader
    var points: ShaderLibShader
    var dashed: ShaderLibShader
    var depth: ShaderLibShader
    var normal: ShaderLibShader
    var sprite: ShaderLibShader
    var background: ShaderLibShader
    var cube: ShaderLibShader
    var equirect: ShaderLibShader
    var distanceRGBA: ShaderLibShader
    var shadow: ShaderLibShader
    var physical: ShaderLibShader
}