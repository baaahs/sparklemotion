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

external open class OutlinePass(resolution: Vector2, scene: Scene, camera: Camera, selectedObjects: Array<Object3D__0> = definedExternally) : Pass {
    open var renderScene: Scene
    open var renderCamera: Camera
    open var selectedObjects: Array<Object3D__0>
    open var visibleEdgeColor: Color
    open var hiddenEdgeColor: Color
    open var edgeGlow: Number
    open var usePatternTexture: Boolean
    open var edgeThickness: Number
    open var edgeStrength: Number
    open var downSampleRatio: Number
    open var pulsePeriod: Number
    open var resolution: Vector2
    open var patternTexture: Texture
    open var maskBufferMaterial: MeshBasicMaterial
    open var renderTargetMaskBuffer: WebGLRenderTarget__0
    open var depthMaterial: MeshDepthMaterial
    open var prepareMaskMaterial: ShaderMaterial
    open var renderTargetDepthBuffer: WebGLRenderTarget__0
    open var renderTargetMaskDownSampleBuffer: WebGLRenderTarget__0
    open var renderTargetBlurBuffer1: WebGLRenderTarget__0
    open var renderTargetBlurBuffer2: WebGLRenderTarget__0
    open var edgeDetectionMaterial: ShaderMaterial
    open var renderTargetEdgeBuffer1: WebGLRenderTarget__0
    open var renderTargetEdgeBuffer2: WebGLRenderTarget__0
    open var separableBlurMaterial1: ShaderMaterial
    open var separableBlurMaterial2: ShaderMaterial
    open var overlayMaterial: ShaderMaterial
    open var copyUniforms: Any?
    open var materialCopy: ShaderMaterial
    open var oldClearColor: Color
    open var oldClearAlpha: Number
    open var fsQuad: FullScreenQuad
    open var tempPulseColor1: Color
    open var tempPulseColor2: Color
    open var textureMatrix: Matrix4
    open fun updateSelectionCache()
    open fun changeVisibilityOfSelectedObjects(bVisible: Boolean)
    open fun changeVisibilityOfNonSelectedObjects(bVisible: Boolean)
    open fun updateTextureMatrix()
    open fun getPrepareMaskMaterial(): ShaderMaterial
    open fun getEdgeDetectionMaterial(): ShaderMaterial
    open fun getSeperableBlurMaterial(): ShaderMaterial
    open fun getOverlayMaterial(): ShaderMaterial
}