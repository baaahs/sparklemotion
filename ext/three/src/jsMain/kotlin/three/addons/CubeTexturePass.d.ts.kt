@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.CubeTexture
import three.Mesh
import three.PerspectiveCamera
import three.Scene

open external class CubeTexturePass(camera: PerspectiveCamera, envMap: CubeTexture = definedExternally, opacity: Number = definedExternally) : Pass {
    open var camera: PerspectiveCamera
    open var cubeShader: Any?
    open var cubeMesh: Mesh<*, *>
    open var envMap: CubeTexture
    open var opacity: Number
    open var cubeScene: Scene
    open var cubeCamera: PerspectiveCamera
}