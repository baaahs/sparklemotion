@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.IUniform__0

external interface `T$120` {
    var tInput: IUniform__0
}

external object GodRaysDepthMaskShader {
    var name: String
    var uniforms: `T$120`
    var vertexShader: String
    var fragmentShader: String
}

external interface `T$121` {
    var tInput: IUniform__0
    var fStepSize: IUniform__0
    var vSunPositionScreenSpace: IUniform__0
}

external object GodRaysGenerateShader {
    var name: String
    var uniforms: `T$121`
    var vertexShader: String
    var fragmentShader: String
}

external interface `T$122` {
    var tColors: IUniform__0
    var tGodRays: IUniform__0
    var fGodRayIntensity: IUniform__0
}

external object GodRaysCombineShader {
    var name: String
    var uniforms: `T$122`
    var vertexShader: String
    var fragmentShader: String
}

external interface `T$123` {
    var vSunPositionScreenSpace: IUniform__0
    var fAspect: IUniform__0
    var sunColor: IUniform__0
    var bgColor: IUniform__0
}

external object GodRaysFakeSunShader {
    var name: String
    var uniforms: `T$123`
    var vertexShader: String
    var fragmentShader: String
}