@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.js

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