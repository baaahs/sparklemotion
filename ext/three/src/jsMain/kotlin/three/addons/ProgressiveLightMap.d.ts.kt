package three.addons

import three.*

external interface UVBoxes {
    var w: Number
    var h: Number
    var index: Number
}

external interface LightMapContainers {
    var basicMat: dynamic /* Material | Array<Material> */
        get() = definedExternally
        set(value) = definedExternally
    var `object`: Object3D
}

open external class ProgressiveLightMap(renderer: WebGLRenderer, res: Number = definedExternally) {
    open var renderer: WebGLRenderer
    open var res: Number
    open var lightMapContainers: Array<LightMapContainers>
    open var compiled: Boolean
    open var scene: Scene
    open var tinyTarget: WebGLRenderTarget<*>
    open var buffer1Active: Boolean
    open var firstUpdate: Boolean
    open var warned: Boolean
    open var progressiveLightMap1: WebGLRenderTarget<*>
    open var progressiveLightMap2: WebGLRenderTarget<*>
    open var uvMat: MeshPhongMaterial
    open var uv_boxes: Array<UVBoxes>
    open var blurringPlane: Mesh<PlaneGeometry, MeshBasicMaterial>
    open var labelMaterial: MeshBasicMaterial
    open var labelPlane: PlaneGeometry
    open var labelMesh: Mesh<PlaneGeometry, MeshBasicMaterial>
    open fun addObjectsToLightMap(objects: Array<Object3D>)
    open fun update(camera: Camera, blendWindow: Number = definedExternally, blurEdges: Boolean = definedExternally)
    open fun showDebugLightmap(visible: Boolean, position: Vector3 = definedExternally)
    open fun _initializeBlurPlane(res: Number, lightMap: Texture? = definedExternally)
}