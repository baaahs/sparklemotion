@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.IUniform__0

external interface `T$153` {
    var alphaMap: IUniform__0
    var ambientLightColor: IUniform__0
    var color: IUniform__0
    var diffuse: IUniform__0
    var directionalLights: IUniform__0
    var directionalShadowMap: IUniform__0
    var directionalShadowMatrix: IUniform__0
    var emissive: IUniform__0
    var hemisphereLights: IUniform__0
    var lightProbe: IUniform__0
    var map: IUniform__0
    var opacity: IUniform__0
    var pointLights: IUniform__0
    var pointShadowMap: IUniform__0
    var pointShadowMatrix: IUniform__0
    var rectAreaLights: IUniform__0
    var shininess: IUniform__0
    var specular: IUniform__0
    var spotLights: IUniform__0
    var spotShadowMap: IUniform__0
    var spotShadowMatrix: IUniform__0
    var thicknessAmbient: IUniform__0
    var thicknessAttenuation: IUniform__0
    var thicknessColor: IUniform__0
    var thicknessDistortion: IUniform__0
    var thicknessMap: IUniform__0
    var thicknessPower: IUniform__0
    var thicknessScale: IUniform__0
    var uvTransform: IUniform__0
}

external object SubsurfaceScatteringShader {
    var name: String
    var uniforms: `T$153`
    var vertexShader: String
    var fragmentShader: String
}