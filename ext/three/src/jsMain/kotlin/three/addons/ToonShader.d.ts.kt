@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.IUniform__0

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