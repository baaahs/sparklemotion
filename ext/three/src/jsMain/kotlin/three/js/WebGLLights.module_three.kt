@file:JsModule("three")
@file:JsNonModule
package three.js

external interface `T$65` {
    var directionalLength: Number
    var pointLength: Number
    var spotLength: Number
    var rectAreaLength: Number
    var hemiLength: Number
    var numDirectionalShadows: Number
    var numPointShadows: Number
    var numSpotShadows: Number
    var numSpotMaps: Number
    var numLightProbes: Number
}

external interface WebGLLightsState {
    var version: Number
    var hash: `T$65`
    var ambient: Array<Number>
    var probe: Array<Any>
    var directional: Array<Any>
    var directionalShadow: Array<Any>
    var directionalShadowMap: Array<Any>
    var directionalShadowMatrix: Array<Any>
    var spot: Array<Any>
    var spotShadow: Array<Any>
    var spotShadowMap: Array<Any>
    var spotShadowMatrix: Array<Any>
    var rectArea: Array<Any>
    var point: Array<Any>
    var pointShadow: Array<Any>
    var pointShadowMap: Array<Any>
    var pointShadowMatrix: Array<Any>
    var hemi: Array<Any>
    var numSpotLightShadowsWithMaps: Number
    var numLightProbes: Number
}

open external class WebGLLights(extensions: WebGLExtensions) {
    open var state: WebGLLightsState
    open fun get(light: Any): Any
    open fun setup(lights: Any)
    open fun setupView(lights: Any, camera: Any)
}