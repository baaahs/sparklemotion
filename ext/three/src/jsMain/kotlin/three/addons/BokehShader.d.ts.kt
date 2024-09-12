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

external interface `T$102` {
    var DEPTH_PACKING: Number
    var PERSPECTIVE_CAMERA: Number
}

external interface `T$103` {
    var tColor: IUniform__0
    var tDepth: IUniform__0
    var focus: IUniform__0
    var aspect: IUniform__0
    var aperture: IUniform__0
    var maxblur: IUniform__0
    var nearClip: IUniform__0
    var farClip: IUniform__0
}

external object BokehShader {
    var name: String
    var defines: `T$102`
    var uniforms: `T$103`
    var vertexShader: String
    var fragmentShader: String
}