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

external interface BokehShaderUniforms {
    var textureWidth: IUniform__0
    var textureHeight: IUniform__0
    var focalDepth: IUniform__0
    var focalLength: IUniform__0
    var fstop: IUniform__0
    var tColor: IUniform__0
    var tDepth: IUniform__0
    var maxblur: IUniform__0
    var showFocus: IUniform__0
    var manualdof: IUniform__0
    var vignetting: IUniform__0
    var depthblur: IUniform__0
    var threshold: IUniform__0
    var gain: IUniform__0
    var bias: IUniform__0
    var fringe: IUniform__0
    var znear: IUniform__0
    var zfar: IUniform__0
    var noise: IUniform__0
    var dithering: IUniform__0
    var pentagon: IUniform__0
    var shaderFocus: IUniform__0
    var focusCoords: IUniform__0
}