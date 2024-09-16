@file:JsQualifier("three.addons.LightProbeGenerator")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons.LightProbeGenerator

import three.CubeTexture
import three.LightProbe
import three.WebGLCubeRenderTarget
import three.WebGLRenderer

external fun fromCubeTexture(cubeTexture: CubeTexture): LightProbe

external fun fromCubeRenderTarget(renderer: WebGLRenderer, cubeRenderTarget: WebGLCubeRenderTarget): LightProbe