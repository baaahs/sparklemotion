package three.js

open external class CubeCamera(near: Number, far: Number, renderTarget: WebGLCubeRenderTarget) : Object3D/*<Object3DEventMap>*/ {
    open var override: Any
    override val type: String /* String | "CubeCamera" */
    open var renderTarget: WebGLCubeRenderTarget
    open var coordinateSystem: Any
    open var activeMipmapLevel: Number
    open fun updateCoordinateSystem()
    open fun update(renderer: WebGLRenderer, scene: Object3D/*<Object3DEventMap>*/)
}