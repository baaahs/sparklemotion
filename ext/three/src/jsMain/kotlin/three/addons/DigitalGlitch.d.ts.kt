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

external interface `T$111` {
    var tDiffuse: IUniform__0
    var tDisp: IUniform__0
    var byp: IUniform__0
    var amount: IUniform__0
    var angle: IUniform__0
    var seed: IUniform__0
    var seed_x: IUniform__0
    var seed_y: IUniform__0
    var distortion_x: IUniform__0
    var distortion_y: IUniform__0
    var col_s: IUniform__0
}

external object DigitalGlitch {
    var uniforms: `T$111`
    var vertexShader: String
    var fragmentShader: String
}