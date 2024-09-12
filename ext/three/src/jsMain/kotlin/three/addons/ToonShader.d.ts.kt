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

external interface `T$154` {
    var uDirLightPos: IUniform__0
    var uDirLightColor: IUniform__0
    var uAmbientLightColor: IUniform__0
    var uBaseColor: IUniform__0
}

external object ToonShader1 {
    var uniforms: `T$154`
    var vertexShader: String
    var fragmentShader: String
}

external interface `T$155` {
    var uDirLightPos: IUniform__0
    var uDirLightColor: IUniform__0
    var uAmbientLightColor: IUniform__0
    var uBaseColor: IUniform__0
    var uLineColor1: IUniform__0
    var uLineColor2: IUniform__0
    var uLineColor3: IUniform__0
    var uLineColor4: IUniform__0
}

external object ToonShader2 {
    var uniforms: `T$155`
    var vertexShader: String
    var fragmentShader: String
}

external object ToonShaderHatching {
    var uniforms: `T$155`
    var vertexShader: String
    var fragmentShader: String
}

external interface `T$156` {
    var uDirLightPos: IUniform__0
    var uDirLightColor: IUniform__0
    var uAmbientLightColor: IUniform__0
    var uBaseColor: IUniform__0
    var uLineColor1: IUniform__0
}

external object ToonShaderDotted {
    var uniforms: `T$156`
    var vertexShader: String
    var fragmentShader: String
}