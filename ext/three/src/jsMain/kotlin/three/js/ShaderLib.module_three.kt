@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.js

import kotlin.js.*
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*

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