package three.addons

import three.*

open external class OutlinePass(resolution: Vector2, scene: Scene, camera: Camera, selectedObjects: Array<Object3D> = definedExternally) : Pass {
    open var renderScene: Scene
    open var renderCamera: Camera
    open var selectedObjects: Array<Object3D>
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
    open var renderTargetMaskBuffer: WebGLRenderTarget<*>
    open var depthMaterial: MeshDepthMaterial
    open var prepareMaskMaterial: ShaderMaterial
    open var renderTargetDepthBuffer: WebGLRenderTarget<*>
    open var renderTargetMaskDownSampleBuffer: WebGLRenderTarget<*>
    open var renderTargetBlurBuffer1: WebGLRenderTarget<*>
    open var renderTargetBlurBuffer2: WebGLRenderTarget<*>
    open var edgeDetectionMaterial: ShaderMaterial
    open var renderTargetEdgeBuffer1: WebGLRenderTarget<*>
    open var renderTargetEdgeBuffer2: WebGLRenderTarget<*>
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