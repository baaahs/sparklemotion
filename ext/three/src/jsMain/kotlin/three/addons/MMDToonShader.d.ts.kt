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