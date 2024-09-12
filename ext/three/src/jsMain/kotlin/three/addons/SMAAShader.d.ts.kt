@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

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

external interface `T$143` {
    var SMAA_THRESHOLD: String
}

external object SMAAEdgesShader {
    var name: String
    var defines: `T$143`
    var uniforms: `T$118`
    var vertexShader: String
    var fragmentShader: String
}

external interface `T$144` {
    var SMAA_MAX_SEARCH_STEPS: String
    var SMAA_AREATEX_MAX_DISTANCE: String
    var SMAA_AREATEX_PIXEL_SIZE: String
    var SMAA_AREATEX_SUBTEX_SIZE: String
}

external interface `T$145` {
    var tDiffuse: IUniform__0
    var tArea: IUniform__0
    var tSearch: IUniform__0
    var resolution: IUniform__0
}

external object SMAAWeightsShader {
    var name: String
    var defines: `T$144`
    var uniforms: `T$145`
    var vertexShader: String
    var fragmentShader: String
}

external interface `T$146` {
    var tDiffuse: IUniform__0
    var tColor: IUniform__0
    var resolution: IUniform__0
}

external object SMAABlendShader {
    var name: String
    var uniforms: `T$146`
    var vertexShader: String
    var fragmentShader: String
}