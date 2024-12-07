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