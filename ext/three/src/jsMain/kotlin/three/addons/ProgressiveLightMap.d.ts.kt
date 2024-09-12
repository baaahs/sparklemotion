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

external interface UVBoxes {
    var w: Number
    var h: Number
    var index: Number
}

external interface LightMapContainers {
    var basicMat: dynamic /* Material | Array<Material> */
        get() = definedExternally
        set(value) = definedExternally
    var `object`: Object3D__0
}

external open class ProgressiveLightMap(renderer: WebGLRenderer, res: Number = definedExternally) {
    open var renderer: WebGLRenderer
    open var res: Number
    open var lightMapContainers: Array<LightMapContainers>
    open var compiled: Boolean
    open var scene: Scene
    open var tinyTarget: WebGLRenderTarget__0
    open var buffer1Active: Boolean
    open var firstUpdate: Boolean
    open var warned: Boolean
    open var progressiveLightMap1: WebGLRenderTarget__0
    open var progressiveLightMap2: WebGLRenderTarget__0
    open var uvMat: MeshPhongMaterial
    open var uv_boxes: Array<UVBoxes>
    open var blurringPlane: Mesh__2<PlaneGeometry, MeshBasicMaterial>
    open var labelMaterial: MeshBasicMaterial
    open var labelPlane: PlaneGeometry
    open var labelMesh: Mesh__2<PlaneGeometry, MeshBasicMaterial>
    open fun addObjectsToLightMap(objects: Array<Object3D__0>)
    open fun update(camera: Camera, blendWindow: Number = definedExternally, blurEdges: Boolean = definedExternally)
    open fun showDebugLightmap(visible: Boolean, position: Vector3 = definedExternally)
    open fun _initializeBlurPlane(res: Number, lightMap: Texture? = definedExternally)
}