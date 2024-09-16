@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.`T$17`

external interface `T$136` {
    var TOON: Boolean
    var MATCAP: Boolean
    var MATCAP_BLENDING_ADD: Boolean
}

external object MMDToonShader {
    var name: String
    var defines: `T$136`
    var uniforms: `T$17`
    var vertexShader: String
    var fragmentShader: String
}