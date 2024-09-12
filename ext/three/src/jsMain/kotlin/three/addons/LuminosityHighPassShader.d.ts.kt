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

external interface `T$134` {
    var tDiffuse: IUniform__0
    var luminosityThreshold: IUniform__0
    var smoothWidth: IUniform__0
    var defaultColor: IUniform__0
    var defaultOpacity: IUniform__0
}

external object LuminosityHighPassShader {
    var name: String
    var shaderID: String
    var uniforms: `T$134`
    var vertexShader: String
    var fragmentShader: String
}