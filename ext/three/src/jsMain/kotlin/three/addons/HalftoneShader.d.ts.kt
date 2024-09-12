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

external interface `T$129` {
    var tDiffuse: IUniform__0
    var shape: IUniform__0
    var radius: IUniform__0
    var rotateR: IUniform__0
    var rotateG: IUniform__0
    var rotateB: IUniform__0
    var scatter: IUniform__0
    var width: IUniform__0
    var height: IUniform__0
    var blending: IUniform__0
    var blendingMode: IUniform__0
    var greyscale: IUniform__0
    var disable: IUniform__0
}

external object HalftoneShader {
    var name: String
    var uniforms: `T$129`
    var vertexShader: String
    var fragmentShader: String
}