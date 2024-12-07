@file:JsQualifier("three.addons.LightProbeGenerator")
package three.addons.LightProbeGenerator

import three.CubeTexture
import three.LightProbe
import three.WebGLCubeRenderTarget
import three.WebGLRenderer

external fun fromCubeTexture(cubeTexture: CubeTexture): LightProbe

external fun fromCubeRenderTarget(renderer: WebGLRenderer, cubeRenderTarget: WebGLCubeRenderTarget): LightProbe